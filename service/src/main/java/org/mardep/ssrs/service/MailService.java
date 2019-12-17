package org.mardep.ssrs.service;

import java.awt.Image;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.dao.codetable.MapAdapter;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRepresentativeDao;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.Action;
import org.mardep.ssrs.domain.codetable.ClassSociety;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.domain.sr.CsrForm;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IDemandNoteGenerator;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jasperreports.engine.JRException;

@Service
@EnableScheduling
public class MailService {

	private Logger logger = LoggerFactory.getLogger(MailService.class);

	private ObjectMapper mapper = new ObjectMapper();
	@Autowired(required=false)
	@Qualifier("mailProps")
	private MapAdapter mailProps;

	@Autowired
	private IOwnerDao ownerDao;

	@Autowired
	private IRepresentativeDao repDao;

	@Autowired
	IJasperReportService jasper;

	@Autowired
	private IDemandNoteHeaderDao dnDao;

	@Autowired
	IDemandNoteGenerator demandNoteGenerator;
	@Autowired
	IDnsService dnsService;
	@Autowired
	IVitalDocClient vitalDocClient;

	@Autowired
	@Qualifier("AIP")
	private IReportGenerator aipGenerator;

	public void sendMail(String to, String subjKey, String contentKey, Map<String, Object> params) throws AddressException, MessagingException {
		sendMail(to, subjKey, contentKey, params, null);
	}

	public void sendMail(String to, String subjKey, String contentKey, Map<String, Object> params, byte[] bytes)
			throws MessagingException, AddressException {
		logger.info("send mail to " + to + " subject " + subjKey);
		if (to == null) {
			logger.info("no email to sent");
			return;
		}

		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", mailProps.get("mail.smtp.host"));
		properties.setProperty("mail.smtp.port", "" + mailProps.get("mail.smtp.port"));
		Session session = Session.getDefaultInstance(properties);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(mailProps.get("mail.smtp.from")));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(mailProps.get(subjKey));
		String text = mailProps.get(contentKey);
		if (text != null) {
			for (String key : params.keySet()) {
				Object value = params.get(key);
				if (value != null) {
					text = text.replace("${"+ key + "}", String.valueOf(value));
				}
			}
		} else {
			text= "";
		}
		message.setText(text, "UTF8");
		if (bytes != null) {
			Multipart multipart = new MimeMultipart();
			BodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(text);
			multipart.addBodyPart(textBodyPart);

			BodyPart messageBodyPart = new MimeBodyPart();
			ByteArrayDataSource source = new ByteArrayDataSource(bytes, "application/pdf");
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("memo.pdf");
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);
		}

		Transport.send(message);
	}

	/**
	 * Email Owner to collect CoR
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void collectCor(RegMaster rm) throws AddressException, MessagingException {
		List<Owner> owners = ownerDao.findByApplId(rm.getApplNo());
		for (Owner owner : owners) {
			sendMail(owner.getEmail(), "MAIL_SUBJ_COR_COLLECTION_OWNER", "mail.content.collectCor", toMap(rm));
		}
	}
	/**
	 * Email Owner to submit ship registration missing document
	 * @param csr
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void submitMissingDoc(RegMaster rm) throws AddressException, MessagingException {
		List<Owner> owners = ownerDao.findByApplId(rm.getApplNo());
		for (Owner owner : owners) {
			sendMail(owner.getEmail(), "MAIL_SUBJ_MISSING_SR_DOC", "mail.content.submitRmMissingDoc", toMap(rm));
		}
	}
	/**
	 * Email Owner to submit CSR missing document
	 * @param owner
	 * @param csr CsrForm
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void submitMissingDoc(CsrForm csr) throws AddressException, MessagingException {
		List<Owner> owners = ownerDao.findByApplId(csr.getApplNo());
		for (Owner owner : owners) {
			sendMail(owner.getEmail(), "MAIL_SUBJ_MISSING_CSR_DOC", "mail.content.submitCSRMissingDoc", toMap(csr));
		}
	}
	/**
	 * Email Owner AIP
	 * @param rm RegMaster
	 * @param params
	 * @throws Exception
	 */
	public void sendOwnerAip(RegMaster rm, Map<String, Object> params) throws Exception {
		List<Owner> owners = ownerDao.findByApplId(rm.getApplNo());

		byte[] byteArray = null;
		try (InputStream resourceAsStream = getClass().getResourceAsStream("/server/report/copy.jpg")) {
			byteArray = IOUtils.toByteArray(resourceAsStream);
		}
		if (byteArray != null) {
			// page 1, 7 , 17
			params.put("image://image3.jpeg", byteArray);
			// page 8
			params.put("image://image5.jpeg", byteArray);
			// page 14, 15
			params.put("image://image6.jpeg", byteArray);
			// page 5
			params.put("image://image4.png", byteArray);
			// page 16
			params.put("image://image7.png", byteArray);
		}

		// ref to co today recipient regName regCName imo offNo callsign phone email asstRegistrar registrar classSoc recdDate shipType proposeRegDate year
		params.put("ref", "HKSR 605/" + rm.getApplNo());
		String to = "";
		for (Owner owner : owners) {
			to = owner.getName() + " (Owner)" + "\n";
		}
		if (to.length() > 0) {
			to = to.substring(0, to.length() - 1);
		}
		params.put("to", to);
		params.put("co", repDao.findById(rm.getApplNo()).getName() + " (Representative Person)");
		params.put("today", new SimpleDateFormat("dd MMMMM yyyy", Locale.ENGLISH).format(new Date()));
		params.put("regName", rm.getRegName());
		params.put("regCName", rm.getRegChiName());
		params.put("imo", rm.getImoNo());
		params.put("offNo", rm.getOffNo());
		params.put("callsign", rm.getCallSign());
		params.put("recdDate", rm.getCreatedDate());
		params.put("shipType", rm.getSurveyShipType());

		params.put("recipient", "applicant"); //
		params.put("phone", "31991000");
		params.put("email", "cm@hkmd");
		params.put("asstRegistrar", "HK asst Re");
		params.put("registrar", "reg");
		params.put("classSoc", "ABCDE ? // df");
		params.put("proposeRegDate", "2047-01-01");
		params.put("year", "2008");

		byte[] aip = aipGenerator.generate(params);

		for (Owner owner : owners) {
			sendMail(owner.getEmail(), "MAIL_SUBJ_AIP", "mail.content.sendOwnerAip", toMap(rm), aip);
		}
	}
	/**
	 * Email Class Society for new CoR, updated CoR
	 * @param soc classification society
	 * @param rm
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void corNotify(ClassSociety soc, RegMaster rm) throws AddressException, MessagingException {
		sendMail(mailProps.get("mail.addr.soc." + soc.getId()), "MAIL_SUBJ_COR_COLLECTION_CLASSSOC", "mail.content.collectCorClassSoc", toMap(rm));
	}

	private Map toMap(Object rm) {
		try {
			byte[] writeValueAsBytes = mapper.writeValueAsBytes(rm);
			Map map = mapper.readValue(writeValueAsBytes, Map.class);
			return map;
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Email Owner to collect generated CSR Document
	 * @param owner
	 * @param rm
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void collectCsr(CsrForm csr) throws AddressException, MessagingException {
		List<Owner> owners = ownerDao.findByApplId(csr.getApplNo());
		for (Owner owner : owners) {
			sendMail(owner.getEmail(), "MAIL_SUBJ_CSR_COLLECTION_OWNER", "mail.content.collectCsr", toMap(csr));
		}
	}

	/**
	 * Email Owner to submit CSR Profolio
	 * @param owner
	 * @param rm
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void submitCsrPortfolio(CsrForm csr) throws AddressException, MessagingException {
		List<Owner> owners = ownerDao.findByApplId(csr.getApplNo());
		for (Owner owner : owners) {
			sendMail(owner.getEmail(), "MAIL_SUBJ_SUBMIT_CSR_PORTFOLIO", "mail.content.submitCsrPortfolio", toMap(csr));
		}
	}
	/**
	 * Memo to OFCA for change of CoR
	 * @throws MessagingException
	 * @throws AddressException
	 * @throws JRException
	 * @throws IOException
	 *
	 */
	public void sendOfcaCorChange (RegMaster rm) throws AddressException, MessagingException, JRException, IOException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("regDate", rm.getRegDate());
		params.put("regName", rm.getRegName());
		// TODO
		params.put("registrar", "CM");
		params.put("post", "assistant");
		params.put("remark", "testing remark is putting here");
		try (InputStream imageRes = new FileInputStream("D:\\mdtrusted-workspace\\workspace\\xmltrunk\\webui\\src\\main\\webapp\\images\\add.png")) {
			Image sign = ImageIO.read(imageRes); // report/images/hkSar.jpg
			params.put("signature", sign);
		}
		params.put("printCover", Boolean.TRUE);
		params.put("ref", "() HKSR 000/2019/001");

		byte[] bytes = jasper.generateReport("PRG-SUPP-019_memoCor.jrxml", Arrays.asList(this), params);

		try (FileOutputStream fos = new FileOutputStream("d:\\Downloads\\report" + System.currentTimeMillis() + ".pdf")) {
			fos.write(bytes);
		}

		sendMail(mailProps.get("mail.ofcaAddr"), "MAIL_SUBJ_COR_CHANGE", "mail.content.sendOfcaCorChange", toMap(rm));
	}
	/**
	 * Memo to CO/SD for change of CoR
	 * @throws MessagingException
	 * @throws AddressException
	 *
	 */
	public void sendCoSdCorChange(RegMaster rm) throws AddressException, MessagingException {
		sendMail(mailProps.get("mail.coSdAddr"), "MAIL_SUBJ_COR_CHANGE", "mail.content.sendCoSdCorChange", toMap(rm));
	}

	/**
	 * Memo to OFCA for AIP, new CoR, updated CoR
	 * @throws MessagingException
	 * @throws AddressException
	 *
	 */
	public void sendOfcaNewOrUpdateCorAip (RegMaster rm) throws AddressException, MessagingException {
		sendMail(mailProps.get("mail.ofcaAddr"), "MAIL_SUBJ_COR_AIP", "mail.content.sendOfcaNewOrUpdateCorAip", toMap(rm));
	}
	/**
	 * Memo to CO/SD for AIP, new CoR, updated CoR
	 * @throws MessagingException
	 * @throws AddressException
	 *
	 */
	public void sendCoSdNewOrUpdateCorAip(RegMaster rm) throws AddressException, MessagingException {
		sendMail(mailProps.get("mail.coSdAddr"), "MAIL_SUBJ_COR_AIP", "mail.content.sendCoSdNewOrUpdateCorAip", toMap(rm));
	}

	public void sendReund(DemandNoteRefund demandNoteRefund) throws AddressException, MessagingException {
		sendMail(mailProps.get("mail.refund.toAddress"), "mail.refund.subject", "mail.refund.content", toMap(demandNoteRefund));
	}

	/**
	 * Send out Demand Note with 1st/2nd reminder
	 * @param count 1 or 2 for 1st or 2nd
	 * @throws s
	 */
	@Scheduled(cron="${MailService.sendDnReminder.cron}")
	@Transactional
	public void sendDnReminder() throws Exception {
		User user = new User();
		user.setId("MailService");
		UserContextThreadLocalHolder.setCurrentUser(user);
		logger.info("sendDnReminder");
		remind(dnDao.listForReminder(IDemandNoteHeaderDao.DEMAND_NOTE_REMINDER_1ST));
		remind(dnDao.listForReminder(IDemandNoteHeaderDao.DEMAND_NOTE_REMINDER_2ND));
	}

	private void remind(List<DemandNoteHeader> reminders) throws Exception {
		Date today = new Date();
		String finance = mailProps.get("mail.finance.email");
		for (DemandNoteHeader h : reminders) {
			Representative rep = repDao.findById(h.getApplNo());
			String email = rep == null || rep.getEmail() == null ? finance : rep.getEmail();
			if (email != null) {
				if (h.getId().length() < 15) {
					continue;
				}
				h = dnDao.findById(h.getId());
				if (h.getFirstReminderDate() == null) {
					h.setFirstReminderDate(today);
					h.setFirstReminderFlag("Y");
				} else {
					h.setSecondReminderDate(today);
					h.setSecondReminderFlag("Y");
				}
				dnDao.save(h);
				String demandNoteNo = h.getDemandNoteNo();
				boolean autopay = "1".equals(h.getEbsFlag());
				byte[] osContent = demandNoteGenerator.generate(demandNoteNo, autopay, h.getSecondReminderDate() == null);
				h.setPdf(osContent);

				if(osContent!=null){
					try {
						vitalDocClient.uploadDemandNote(demandNoteNo, osContent);
					} catch (Exception e) {
						logger.error("fail to upload reminder to dms " + email, e);
					}
				}

				try {
					sendMail(email, "MAIL_SUBJ_DN_REMINDER", "mail.content.sendDnReminder", toMap(h), osContent);
				} catch (Exception e) {
					logger.error("fail to email reminder to " + email, e);
				}
			}
		}
	}

}
