package org.mardep.ssrs.report.generator;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.mardep.ssrs.dao.codetable.IFeeCodeDao;
import org.mardep.ssrs.dao.codetable.IReasonCodeDao;
import org.mardep.ssrs.dao.codetable.IRegistrarDao;
import org.mardep.ssrs.dao.sr.IBuilderMakerDao;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.codetable.ReasonCode;
import org.mardep.ssrs.domain.codetable.ReasonCodePK;
import org.mardep.ssrs.domain.codetable.Registrar;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.domain.sr.Mortgagor;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.EnglishReportDateFormat;
import org.mardep.ssrs.report.EnglishReportLongDateFormat;
import org.mardep.ssrs.service.IDemandNoteService;
import org.mardep.ssrs.service.IMortgageService;
import org.mardep.ssrs.service.IShipRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;

@Service("RPT_SR_011")
public class RPT_SR_011 extends AbstractSrReport {

	@Autowired
	IBuilderMakerDao bmDao;

	@Autowired
	IRegistrarDao rDao;

	@Autowired
	IShipRegService service;

	@Autowired
	IMortgageService mortgageSrv;

	@Autowired
	IReasonCodeDao reasons;

	@Autowired
	IDemandNoteService demandNoteService;

	@Autowired
	IFeeCodeDao fcDao;

	public RPT_SR_011() {
		super("RPT-SR-011.jrxml", null);
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		if (Boolean.TRUE.equals(inputParam.get("paymentRequired"))) {
			String fcId = Boolean.TRUE.equals(inputParam.get("certified")) ? "14" : "11";
			FeeCode code = fcDao.findById(fcId);
			Date generationTime = new Date();
			for (String applNo : ((String) inputParam.get("applNo")).split("\\,")) {
				DemandNoteItem entity = new DemandNoteItem();
				entity.setActive(true);
				entity.setAmount(code.getFeePrice());
				entity.setApplNo(applNo);
				entity.setChargedUnits(1);
				entity.setFcFeeCode(code.getId());
				entity.setGenerationTime(generationTime);
				demandNoteService.addItem(entity);
			}
		}
		Date reportDate = (Date)inputParam.get("reportDate");
		if (System.currentTimeMillis() - reportDate.getTime() < 60000) {
			reportDate = new Date(reportDate.getTime() + 60000); // add some delay for showing latest tx
		}
		String applNoList = (String) inputParam.get("applNo");
		boolean printMortgage = Boolean.TRUE.equals(inputParam.get("printMortgage"));
		logger.info("Report Date:{}", reportDate);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("certified", inputParam.get("certified"));
		if (Boolean.TRUE.equals(inputParam.get("printLogo"))) {
			params.put("logo", RPT_SR_011.class.getResource("/images/400px-Regional_Emblem_of_Hong_Kong.svg.png"));
		}
		params.put("reportDate", new EnglishReportDateFormat().format(reportDate) + " " + new SimpleDateFormat("HH:mm").format(reportDate));
		params.put("reportDateChi", new SimpleDateFormat("y\u5e74M\u6708d\u65e5HH\u6642mm\u5206", Locale.CHINESE).format(reportDate));
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		params.put("userId", currentUser);
		if (printMortgage) {
			params.put("SUBREPORT_2", jasperReportService.getJasperReport("RPT-SR-011-subreport02.jrxml"));
			params.put("printMortgage", Boolean.TRUE);
		}
		params.put("SUBREPORT_1", jasperReportService.getJasperReport("RPT-SR-011-subreport01.jrxml"));

		Date issueDate = new Date();
		params.put("issueTime", new EnglishReportLongDateFormat().format(issueDate) + " at " + new SimpleDateFormat("HH:mm").format(issueDate));
		params.put("issueTimeChi", new SimpleDateFormat("y\u5e74M\u6708d\u65e5HH\u6642mm\u5206", Locale.CHINESE).format(issueDate));
		Long registrarId = (Long) inputParam.get("registrar");
		if (registrarId != null) {
			Registrar registrar = rDao.findById(registrarId);
			params.put("registrar", registrar.getName());
		} else {
			params.put("registrar", "");
		}

		if (applNoList.contains(",")) {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ZipOutputStream zip = new ZipOutputStream(baos);
				for (String applNo : applNoList.split("\\,")) {
					byte[] pdf = getPdf(reportDate, printMortgage, params, applNo);
					String name = applNo.replace('/', '_')+ ".pdf";
					ZipEntry entry = new ZipEntry(name);
					zip.putNextEntry(entry);
					zip.write(pdf);
					zip.closeEntry();
				}
				zip.finish();
				zip.flush();
				return baos.toByteArray();
			}
		} else {
			return getPdf(reportDate, printMortgage, params, applNoList);
		}
	}

	private byte[] getPdf(Date reportDate, boolean printMortgage, Map<String, Object> params, String applNo)
			throws JRException, ParseException {
		return jasperReportService.generateReport(getReportFileName(), Arrays.asList(getReportContent(applNo, reportDate, printMortgage)), params);
	}

	private HashMap<Object, Object> getReportContent(String applNo, Date reportDate, boolean printMortgage) throws ParseException {
		//RegMaster regM = service.findById(applNo, reportDate);
		RegMaster regM = service.findById(RegMaster.class, applNo);
		HashMap<Object, Object> regMaster = new HashMap<>();
		if (regM == null) {
			return regMaster;
		}
		regMaster.put("regMaster", regM);
		regMaster.put("shipName", regM.getRegName());

		if (regM.getRcReasonCode() != null && regM.getRcReasonType() != null) {
			ReasonCodePK id = new ReasonCodePK(regM.getRcReasonCode(), regM.getRcReasonType());
			ReasonCode reason = reasons.findById(id);
			if (reason != null) {
				regMaster.put("reason", reason.getEngDesc());
			}
		}
		Long registrarId = regM.getRegistrar();
		if (registrarId != null) {
			Registrar registrar = rDao.findById(registrarId);
			regMaster.put("registrar", registrar.getName());
		} else {
			regMaster.put("registrar", "");
		}

		regMaster.put("imoNo", isNull(regM.getImoNo(), "-"));
		regMaster.put("callSign", isNull(regM.getCallSign(), "-"));
		regMaster.put("officialNo", regM.getOffNo());
		regMaster.put("materialOfHull", regM.getMaterial());
		regMaster.put("grossTonnage", new DecimalFormat("#,### tons").format(isNull(regM.getGrossTon(), BigDecimal.ZERO)));
		regMaster.put("howPropelled", 
				regM.getHowProp()!=null ? regM.getHowProp() : "N/A");
		regMaster.put("netTonnage", new DecimalFormat("#,### tons").format(isNull(regM.getRegNetTon(), BigDecimal.ZERO)));

		regMaster.put("noOfSetsEngine", 
				regM.getEngSetNum()!=null ? String.valueOf(regM.getEngSetNum()) : "0");
		regMaster.put("totalEnginePower", 
				regM.getEngPower()!=null ? regM.getEngPower() : "N/A" );

		regMaster.put("portOfReg", "HONG KONG");
		regMaster.put("portOfRegChi", "\u9999\u6e2f");

		regMaster.put("dateKeelLaid", regM.getBuildDate());
		//List<BuilderMaker> blders = service.findBuilders(applNo, reportDate);
		List<BuilderMaker> blders = service.findBuildersByApplId(applNo);
		if (!blders.isEmpty()) {
			BuilderMaker builder = blders.get(0);
			regMaster.put("builder", builder);
		}

		regMaster.put("mainEngineType", 
				regM.getEngDesc1()!=null ? regM.getEngDesc1() : "N/A");
		// 20190807 set default to N/A if engine make and model is null
		String engineMakeAndModel = "";
		if (regM.getEngModel1()!=null) {
			engineMakeAndModel = regM.getEngModel1() + "\n";
		}
		if (regM.getEngModel2()!=null) {
			engineMakeAndModel = engineMakeAndModel + regM.getEngModel2();
		}
		if (engineMakeAndModel.isEmpty()) {
			engineMakeAndModel = "N/A";
		}
		regMaster.put("engineMakeAndModel", engineMakeAndModel);

		//Representative rep = service.findRepById(applNo, reportDate);
		Representative rep = service.findRpByApplId(applNo);
		if (rep != null) {
			regMaster.put("repName", rep.getName());
			regMaster.put("repAddress",
					(rep.getAddress1() == null ? "" : rep.getAddress1()) + "\n" +
							(rep.getAddress2() == null ? "" : rep.getAddress2()) + "\n" +
							(rep.getAddress3() == null ? "" : rep.getAddress3()));
		}
		List<Map<String, ?>> dataList = new ArrayList<Map<String, ?>>();
		//List<Owner> owners = service.findOwners(applNo, reportDate);
		List<Owner> owners = service.findOwnersByApplId(applNo);
		regMaster.put("demiseDetails", "");
		regMaster.put("ownerDetails", "");
		for (int i = 0; i < owners.size(); i++) {
			Owner owner = owners.get(i);
			boolean demise = Owner.TYPE_DEMISE.equals(owner.getType());
			if (demise) {
				regMaster.put("demiseEDate", owner.getCharterEdate());
				regMaster.put("demiseSDate", owner.getCharterSdate());
				regMaster.put("demisePlace", owner.getIncorpPlace());
			}
			HashMap<String, Object> subreportRow = new HashMap<>();
			subreportRow.put("ownerName", owner.getName());
			subreportRow.put("ownerStatus", owner.getStatus());
			// 2019.07.16 output address in one line
			String ownerAddr = (owner.getAddress1() != null ? owner.getAddress1() :"") +
					(owner.getAddress2()!=null ? " " + owner.getAddress2() : "") +
					(owner.getAddress3()!=null ? " " + owner.getAddress3() : "");
//			String ownerAddr = (owner.getAddress1() != null ? owner.getAddress1() :"") + "\n" +
//					(owner.getAddress2() != null ? owner.getAddress2() :"") + "\n" +
//					(owner.getAddress3() != null ? owner.getAddress3() :"");
			subreportRow.put("ownerAddress", ownerAddr);
			// 2019.07.16 remove output of percentage and place of incorporation
			// add again by jira SSRS-104
			subreportRow.put("percentage", owner.getIntMixed() == null ?
					(owner.getIntNumberator() != null ? owner.getIntNumberator() : "-") + "/" +
					(owner.getIntDenominator() != null ? owner.getIntDenominator() : "-") :
						owner.getIntMixed().toString());
			subreportRow.put("placeOfIncorp", "Y".equals(owner.getQualified()) ? "HONG KONG" : owner.getIncorpPlace());

			String ownerStr = "";
			if (owner.getName() != null) {
				ownerStr = owner.getName() + "\n" + ownerAddr;
			}
			regMaster.put(demise ? "demiseDetails" : "ownerDetails", ownerStr);
			dataList.add(subreportRow);
		}
		regMaster.put("owners", dataList);

		processMortgages(applNo, reportDate, printMortgage, regMaster, owners);
		return regMaster;
	}

	protected void processMortgages(String applNo, Date reportDate, boolean printMortgage,
			HashMap<Object, Object> regMaster, List<Owner> owners) throws ParseException {
		if (printMortgage) {
			List<Map<String, ?>> dataList2 = new ArrayList<Map<String, ?>>();
			//List<Mortgage> mortgages = mortgageSrv.findMortgages(applNo, reportDate);
			List<Mortgage> mortgages = mortgageSrv.findMortgagesByApplId(applNo);
			Map<String, String> natures = mortgageSrv.findMortgageRegDateNatures(applNo);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ddHHmm");
			SimpleDateFormat rptDf = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.ENGLISH);

			for (int i = 0; i < mortgages.size(); i++) {
				Mortgage mortgage = mortgages.get(i);
				Date regTime = mortgage.getRegTime();
				if (regTime == null || "D".equals(mortgage.getMortStatus())) {
					continue;
				}

				HashMap<String, Object> subreportRow = new HashMap<>();
				subreportRow.put("mortgageCode", mortgage.getPriorityCode());
				String pc;
				switch (i) {
				case 0:
					pc = "(1st Priority Code)";
					break;
				case 1:
					pc = "(2nd Priority Code)";
					break;
				case 2:
					pc = "(3rd Priority Code)";
					break;
				default:
					pc = "(" + (i+1) + "th Priority Code)";
					break;
				}
				subreportRow.put("priorityCode", pc);
				//List<Mortgagor> mortgagors = mortgageSrv.findMortgagors(mortgage, reportDate);
				List<Mortgagor> mortgagors = mortgageSrv.findMortgagorsByMortgage(mortgage);
				StringBuilder name = new StringBuilder();
				StringBuilder place = new StringBuilder();
				mortgagors.forEach(m -> {
					for (Owner owner : owners) {
						if (owner.getOwnerSeqNo().equals(m.getSeq())) {
							name.append(owner.getName()).append(", ");
							place.append(owner.getRegPlace() != null ? owner.getRegPlace() : owner.getIncorpPlace()).append(", ");
							subreportRow.put("propertyOfShip", owner.getIntMixed() + " Percentage");
							break;
						}
					}
				});

				if (name.length() >= 2) {
					name.delete(name.length() - 2, name.length());
				}
				if (place.length() >= 2) {
					place.delete(place.length() - 2, place.length());
				}
				subreportRow.put("mortgagorName", name.toString());
				subreportRow.put("placeOfIncorp", place.toString());

				//List<Mortgagee> mortgagees = mortgageSrv.findMortgagees(mortgage, reportDate);
				List<Mortgagee> mortgagees = mortgageSrv.findMortgageesByMortgage(mortgage);
				StringBuilder geeNames = new StringBuilder();
				StringBuilder addresses = new StringBuilder();
				StringBuilder tel = new StringBuilder();
				StringBuilder fax = new StringBuilder();
				StringBuilder email = new StringBuilder();

				for (Mortgagee mortgagee : mortgagees) {
					geeNames.append(mortgagee.getName()).append(", ");
					addresses.append(getAddress(mortgagee.getAddress1(), mortgagee.getAddress2(), mortgagee.getAddress3())).append("\n");
					tel.append(isNull(mortgagee.getTelNo(), "-")).append(", ");
					fax.append(isNull(mortgagee.getFaxNo(), "-")).append(", ");
					email.append(isNull(mortgagee.getEmail(), "-")).append(", ");
				}
				if (geeNames.length() >= 2) {
					geeNames.delete(geeNames.length() - 2, geeNames.length());
				}
				if (addresses.length() >= 1) {
					addresses.delete(addresses.length() - 1, addresses.length());
				}
				if (tel.length() >= 2) {
					tel.delete(tel.length() - 2, tel.length());
				}
				if (fax.length() >= 2) {
					fax.delete(fax.length() - 2, fax.length());
				}
				if (email.length() >= 2) {
					email.delete(email.length() - 2, email.length());
				}
				subreportRow.put("mortgageeName", geeNames.toString());
				subreportRow.put("mortgageeAddress", addresses.toString());
				subreportRow.put("telNo", tel.toString());
				subreportRow.put("faxNo", fax.toString());
				subreportRow.put("email", email.toString());

				String string = natures.get(mortgage.getPriorityCode());
				if (string != null && string.contains("\n")) {
					int indexOf = string.indexOf("\n");
					String dateStr = string.substring(0, indexOf);
					Date date = df.parse(dateStr);
					subreportRow.put("regDate", rptDf.format(date));
					subreportRow.put("nature", mortgage.getAgreeTxt());
				}
				dataList2.add(subreportRow);
			}
			regMaster.put("mortgages", dataList2);
		}
	}

	private String getAddress(String... lines) {
		String result = "";
		for (String line : lines) {
			if (line != null) {
				result += " " + line;
			}
		}
		return result.trim();
	}

	private Object isNull(Object value, Object replacement) {
		return value != null ? value : replacement;
	}

}
