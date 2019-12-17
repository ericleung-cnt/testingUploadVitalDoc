package org.mardep.ssrs.vitaldoc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.codetable.ISystemParamDao;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.dao.dns.IControlDataDao;
import org.mardep.ssrs.dns.IDnsOutService;
import org.mardep.ssrs.dns.pojo.common.CreateReceiptAction;
import org.mardep.ssrs.dns.pojo.common.ReceiptItem;
import org.mardep.ssrs.dns.pojo.outbound.RequestFile;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.Action;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteInfo;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteItem;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteRequest;
import org.mardep.ssrs.dns.pojo.outbound.createDemandNote.DemandNoteResponse;
import org.mardep.ssrs.dns.pojo.outbound.createReceipt.CreateReceiptInfo;
import org.mardep.ssrs.dns.pojo.outbound.createReceipt.ReceiptRequest;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.codetable.SystemParam;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.dns.ControlAction;
import org.mardep.ssrs.domain.dns.ControlData;
import org.mardep.ssrs.domain.dns.ControlEntity;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IDemandNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class DemandNoteMigration {

	@Autowired
	@Qualifier("demandNoteService")
	IDemandNoteService dns;

	@Autowired
	IDnsOutService dnsOutService;

	@Autowired
	IDemandNoteItemDao demandNoteItemDao;

	@Autowired
	ISystemParamDao systemParamDao;

	@Autowired
	IControlDataDao controlDataDao;

	@Autowired
	IDemandNoteHeaderDao headerDao;

	/**
	 * step 3 read demand note from sql server and send to dns
	 * @throws ParseException
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void test() throws ParseException {
		User user = new User();
		user.setId("DM");
		UserContextThreadLocalHolder.setCurrentUser(user);
		DemandNoteHeader entity = new DemandNoteHeader();
		entity.setPaymentStatus(null);
		entity.setStatus(null);
		List<DemandNoteHeader> headers = dns.findByCriteria(entity);
		System.out.println("headers.size() ="+ headers.size());

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date start = format.parse("20180401");
		Date end = format.parse("20191231");
		Date adjustTime = format.parse("20190921");
		int receiptCount = 0;
		int dnCount = 0;

		List<String> failedDn = new ArrayList<>();
		List<String> failedRe = new ArrayList<>();
		SystemParam sp = systemParamDao.findById("RECEIPT_NO_SEQ");
		int nextVal = (Integer.parseInt(sp.getValue()) + 1) % 100000;
		DecimalFormat receiptDf = new DecimalFormat("0000000");

		for (DemandNoteHeader header : headers) {
			String original = header.getDemandNoteNo();
			if (original.length() == 15) {
				continue;
			}
			if (header.getAdjustReason() != null) {
				continue;
			}
			DemandNoteReceipt receiptCriteria = new DemandNoteReceipt();
			receiptCriteria.setDemandNoteNo(original);
			List<DemandNoteReceipt> receipts = dns.findByCriteria(receiptCriteria);
			boolean send = false;
			if (receipts.isEmpty()) {
				send = true;
			} else {
				for (DemandNoteReceipt receipt : receipts) {
					if (!receipt.getInputTime().before(start) && receipt.getInputTime().before(end)) {
						send = true;
						break;
					}
				}
			}
			if (!send) {
				System.out.println("not send " + original);
				continue;
			}
			System.out.println("send " + original);

			String officeCode = header.getApplNo() != null && header.getApplNo().length() > 1 ? Cons.SSRS_SR_OFFICE_CODE : Cons.SSRS_MMO_OFFICE_CODE;
			// create new demand note no. (length 15)
			String demandNoteNo = dns.getDemandNoteNumber(Cons.DNS_BILL_CODE, officeCode);
			ControlData controlData = new ControlData();
			controlData.setEntity(ControlEntity.DN.getCode());
			controlData.setAction(ControlAction.CREATE.getCode());
			controlData.setEntityId(demandNoteNo);

			ControlData newCD = controlDataDao.save(controlData);
			List<org.mardep.ssrs.domain.dn.DemandNoteItem> demandNoteItemList = demandNoteItemDao.findByDemandNoteNo(original);
			try {
				sendDemandNote(header, officeCode, demandNoteNo, newCD, demandNoteItemList);
			} catch (Exception e) {
				failedDn.add(original);
				continue;
			}
			dnCount++;

			header.setAdjustReason(demandNoteNo);
			header.setAdjustTime(adjustTime);
			headerDao.save(header);

			for (DemandNoteReceipt dnr : receipts) {

				ReceiptRequest req = new ReceiptRequest();
				ControlData cd = new ControlData();
				cd.setEntity(ControlEntity.RECEIPT.getCode());
				cd.setAction(ControlAction.CREATE.getCode());
				String next = String.valueOf(nextVal++);
				if (nextVal % 100 == 0) {
					sp.setValue(next);
					systemParamDao.save(sp);
				}
				String receiptNo = "ES" + receiptDf.format(nextVal);


				cd.setEntityId(receiptNo);
				cd = controlDataDao.save(cd);
				req.setControlId(Long.toString(cd.getId()));

				req.setCreateReceiptAction(CreateReceiptAction.U);
				CreateReceiptInfo info = new CreateReceiptInfo();
				info.setBillCode("05"); // SSRS
				info.setDnNo(demandNoteNo);
				info.setMachineID("");
				ArrayList<ReceiptItem> paymentList = new ArrayList<>();
				ReceiptItem item = new ReceiptItem();
				item.setPaymentAmount(dnr.getAmount());
				String paymentType = "50";
				if (header.getEbsFlag() == null) {
					paymentType = "10";
				} else {
					switch (header.getEbsFlag()) {
					case "2":
						paymentType = "10";
						break;
					case "6":
						paymentType = "90";
						break;
					}
				}

				item.setPaymentType(paymentType);
				paymentList.add(item);
				info.setPaymentList(paymentList);
				info.setReceiptAmount(dnr.getAmount());
				info.setReceiptDate(dnr.getCreatedDate());


				info.setReceiptNo(receiptNo);
				req.setReceiptInfo(info);

				try {
					dnsOutService.sendReceipt(req);
				} catch (Exception e) {
					failedRe.add(receiptNo);
					continue;
				}
				receiptCount++;
			}
			if (dnCount >= 1200) {
				break;
			}
		}
		String next = String.valueOf(nextVal);
		sp.setValue(next);
		systemParamDao.save(sp);

		System.out.println("dn count = " + dnCount);
		System.out.println("receipt count = " + receiptCount);
		System.out.println("failed dn size "+failedDn.size());
		System.out.println("failed re size "+failedRe.size());
		System.out.println("failed dn "+failedDn);
		System.out.println("failed re "+failedRe);

	}

	private void sendDemandNote(DemandNoteHeader header, String officeCode, String demandNoteNo, ControlData newCD,
			List<org.mardep.ssrs.domain.dn.DemandNoteItem> demandNoteItemList) {
		DemandNoteRequest request = new DemandNoteRequest();
		//Header info
		request.setAction(Action.U);
		request.setControlId(Long.toString(newCD.getId()));
		RequestFile rf = new RequestFile();
		rf.setdFile(newCD.getFile());
		request.setFile(rf);

		//Body Info
		DemandNoteInfo info = new DemandNoteInfo();
		info.setDnNo(demandNoteNo);
		info.setLastUpdateDatetime(header.getUpdatedDate());
		info.setUserCode(header.getUpdatedBy());
		info.setIssueDate(DateUtils.truncate(header.getGenerationTime(), Calendar.DATE));
		info.setAmountTTL(header.getAmount());
		info.setBillCode(Cons.DNS_BILL_CODE);
		info.setOfficeCode(officeCode);
		info.setPayerName(header.getBillName() != null && header.getBillName().length() > 120 ? header.getBillName().substring(0,  120) : header.getBillName());
		info.setRemarks(header.getCwRemark());
		info.setAutopayRequest(0);
		info.setIsAutopay(0);
		info.setDueDate(header.getDueDate());
		info.setDnStatus(3);
		//Charge Items
		List<DemandNoteItem> itemList = new ArrayList<DemandNoteItem>();
		int[] idx = {0};
		demandNoteItemList.forEach(dni -> {
			idx[0]++;
			itemList.add(construct(idx[0], dni));
		});

		info.setItemList(itemList);
		request.setDemandNoteInfo(info);
		DemandNoteResponse result = dnsOutService.sendDemandNote(request);
		if (result.getBaseResult() != null) {
			// TODO
		}
	}

	private DemandNoteItem construct(int idx, org.mardep.ssrs.domain.dn.DemandNoteItem dni){
		DemandNoteItem item = new DemandNoteItem();
    	item.setItemNo(idx);
    	item.setIsRemark(0);
    	if(dni.getFcFeeCode()!=null && dni.getFeeCode()!=null){
    		FeeCode feeCode = dni.getFeeCode();
    		item.setParticular(feeCode.getEngDesc());
    		item.setRevenueType(feeCode.getFormCode());
    		item.setFeeCode(feeCode.getId());
    		item.setUnitPrice(feeCode.getFeePrice());
    	}
    	item.setUnit(dni.getChargedUnits());
    	item.setAmount(dni.getAmount());
    	return item;
	}

	/**
	 * step 1 read demand note from oracle and write to files
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@Test
	public void readOracle() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		String driverClass = "oracle.jdbc.driver.OracleDriver";
		String orclUrl = "jdbc:oracle:thin:@10.37.108.131:1521:srisuat";
		String orclUser = "sris";
		String orclPwd = "srissris";

		orclUrl = "jdbc:oracle:thin:@10.37.47.101:1522:srisprod";
		orclPwd = "sris2119";

		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
		String ih = "select RM_APPL_NO, RM_APPL_NO_SUF, INVOICE_NO, GENERATION_TIME, BILL_NAME1, BILL_NAME2, CO_NAME1, CO_NAME2, ADDRESS1, " +
				"ADDRESS2, AMOUNT, ADDRESS3, ADJUST_REASON, ADJUST_TIME, RCP_AMOUNT, ACCT_AMOUNT, CW_TIME, CW_STATUS, CW_REMARK, CW_BY, " +
				"EBS_FLAG, DUE_DATE, FIRST_REMINDER_FLAG, FIRST_REMINDER_DATE, SECOND_REMINDER_FLAG, SECOND_REMINDER_DATE " +
				"from INVOICE_HEADERS IH ";

		String ii = "select ITEM_NO, RM_APPL_NO, RM_APPL_NO_SUF, INV_INVOICE_NO, CHARGED_UNITS, AMOUNT, CHG_INDICATOR, ADHOC_INVOICE_TEXT, USER_ID, GENERATION_TIME, FC_FEE_CODE, FORM_SEQ_NO from INVOICE_ITEMS II";

		String ir = "select INV_RM_APPL_NO, INV_RM_APPL_NO_SUF, INV_INVOICE_NO, RECEIPT_NO, BATCH_NO, INPUT_TIME, AMOUNT, CAN_ADJ_STATUS, " +
				"CAN_ADJ_TIME, CAN_ADJ_REMARK, CAN_ADJ_BY, ACCOUNTED " +
				"from INVOICE_RECEIPTS IR ";
		try (Connection connection = DriverManager.getConnection(orclUrl, orclUser, orclPwd)) {
			select(connection, ih);
			select(connection, ii);
			select(connection, ir);
		}
	}

	private void select(Connection connection, String sql) throws SQLException, IOException {
		String fileName = "target/" + sql.substring(sql.indexOf("from ") + 5, sql.indexOf(" ", sql.indexOf("from ") + 5)).trim() + ".data";
		String[] cols = sql.substring(7, sql.indexOf(" from")).split("\\,");
		ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
		try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
			try (ResultSet rs = prepareStatement.executeQuery()) {
				while (rs.next()) {
					HashMap<String, Object> row = new HashMap<>();
					arrayList.add(row);
					for (int i = 1; i <= cols.length; i++) {
						row.put(cols[i-1].trim(), rs.getObject(i));
					}
				}
			}
		}
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
			out.writeObject(arrayList);
		}
	}

	/**
	 * step 2 read demand note from file and write to sql server
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void writeSqlServer() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, ParseException {
		String driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String url = "jdbc:sqlserver://10.37.115.145:1433;databaseName=SSRS";
		String user = "ssrs_user";
		String pwd = "Q1w2e3r4t5";

		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());

		List<Map<String, Object>> headers;
		Map<BigDecimal, List<Map<String, Object>>> receiptMap = new HashMap<>();
		Map<BigDecimal, List<Map<String, Object>>> itemMap = new HashMap<>();

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("target/INVOICE_HEADERS.data"))) {
			headers = (List<Map<String, Object>>) in.readObject();
			System.out.println("headers count = " + headers.size());
		}
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("target/INVOICE_RECEIPTS.data"))) {
			List<Map<String, Object>> receipts = (List<Map<String, Object>>) in.readObject();
			System.out.println("receipt count = " + receipts.size());
			for (Map<String, Object> receipt : receipts) {
				BigDecimal key = (BigDecimal) receipt.get("INV_INVOICE_NO");
				//CAN_ADJ_REMARK=null, CAN_ADJ_TIME=null, BATCH_NO=2019-01-10 10:59:34.0, CAN_ADJ_BY=null, INPUT_TIME=2019-01-10 10:59:34.0, CAN_ADJ_STATUS=null, INV_INVOICE_NO=98959, AMOUNT=260, INV_RM_APPL_NO=2011/405, INV_RM_APPL_NO_SUF=F, RECEIPT_NO=11519338, ACCOUNTED=Y
				List<Map<String, Object>> list = receiptMap.get(key);
				if (list == null) {
					list = new ArrayList<>();
					receiptMap.put(key, list);
				}
				list.add(receipt);
			}
		}
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("target/INVOICE_ITEMS.data"))) {
			List<Map<String, Object>> items = (List<Map<String, Object>>) in.readObject();
			System.out.println("items count = " + items.size());
			for (Map<String, Object> item : items) {
				BigDecimal key = (BigDecimal) item.get("INV_INVOICE_NO");
				List<Map<String, Object>> list = itemMap.get(key);
				if (list == null) {
					list = new ArrayList<>();
					itemMap.put(key, list);
				}
				list.add(item);
			}
		}
		Date start = new SimpleDateFormat("yyyyMMdd").parse("20180401");
		int dhCount = 0;
		int recCount = 0;
		int itemCount = 0;
		try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
			try (PreparedStatement dhInsert = connection.prepareStatement("INSERT INTO DEMAND_NOTE_HEADERS ( " +
					"RM_APPL_NO,RM_APPL_NO_SUF,DEMAND_NOTE_NO,GENERATION_TIME,ADDRESS1,ADDRESS2, " +
					"ADDRESS3,AMOUNT, " + // 8
					"RCP_AMOUNT,ACCT_AMOUNT,CW_TIME,CW_STATUS,CW_BY,EBS_FLAG, " +
					"FIRST_REMINDER_FLAG,FIRST_REMINDER_DATE,SECOND_REMINDER_FLAG,SECOND_REMINDER_DATE, " +//18
					"CREATE_BY,CREATE_DATE,LASTUPD_BY, " +
					"LASTUPD_DATE,ROWVERSION,CW_REMARK,DUE_DATE,CO_NAME, " +
					"BILL_NAME) VALUES ( " +//25
					"?,?,?,?,?,?, " +
					"?,?, " +
					"?,?,?,?,?,?, " +
					"?,?,?,?, " +
					"'DM','2019-09-21','DM', " +
					"'2019-09-21',0,?,?,?, " +
					"? " +
					"); ")) {
				try (PreparedStatement receiptIns = connection.prepareStatement("insert into DEMAND_NOTE_RECEIPTS ("
						+ "CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, "
						+ "ACCOUNTED, AMOUNT, DN_RM_APPL_NO, DN_RM_APPL_NO_SUF, BATCH_NO, "
						+ "CAN_ADJ_BY, CAN_ADJ_REMARK, CAN_ADJ_STATUS, CAN_ADJ_TIME, DN_DEMAND_NOTE_NO, "
						+ "INPUT_TIME, RECEIPT_NO  "
						+ ") values ("
						+ "'DM','2019-09-21','DM', '2019-09-21', 0, "
						+ "?,?,?,?,?, "
						+ "?,?,?,?,?, "
						+ "?,?"
						+ ")")) {
					try (PreparedStatement itemIns = connection.prepareStatement(
							"insert into DEMAND_NOTE_ITEMS ("
							+ "CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, "
							+ "ADHOC_DEMAND_NOTE_TEXT, AMOUNT, RM_APPL_NO, RM_APPL_NO_SUF, CHARGED_UNITS, "
							+ "CHG_INDICATOR, DN_DEMAND_NOTE_NO, FC_FEE_CODE, GENERATION_TIME, ITEM_NO, "
							+ "USER_ID, ACTIVE"
							+ ") values ("
							+ "'DM','2019-09-21','DM', '2019-09-21', 0,"
							+ "?,?,?,?,?, "
							+ "?,?,?,?,?, "
							+ "?, 1) ")){
						for (Map<String, Object> header : headers) {
							BigDecimal invoiceNo = (BigDecimal) header.get("INVOICE_NO");
							Date generationTime = (Date) header.get("GENERATION_TIME");
							Number headerAmount = (Number) header.get("AMOUNT");
							List<Map<String, Object>> list = receiptMap.get(invoiceNo);
							boolean include = false;
							if (generationTime.after(start)) {
								// include
								include= true;
							} else  if (list == null) {
								// outstanding, include
								include= true;
							} else {
								BigDecimal received = BigDecimal.ZERO;
								for (Map<String, Object> receipt : list) {
									received = received.add((BigDecimal) receipt.get("AMOUNT"));
								}
								if (received.equals(headerAmount)) {
									// paid, skip
								} else {
									// include
									include= true;
								}
							}
							if (include) {
								// write header and receipt
								String applNo = (String) header.get("RM_APPL_NO");
								String suf = (String) header.get("RM_APPL_NO_SUF");
								String cwRemark = (String) header.get("CW_REMARK");
								dhInsert.setString(1, applNo);
								dhInsert.setString(2, suf);
								dhInsert.setString(3, (invoiceNo).toString());
								dhInsert.setDate(4, getDate(header, "GENERATION_TIME"));
								dhInsert.setString(5, (String) header.get("ADDRESS1"));
								dhInsert.setString(6, (String) header.get("ADDRESS2"));

								dhInsert.setString(7, (String) header.get("ADDRESS3"));
								dhInsert.setBigDecimal(8, (BigDecimal) header.get("AMOUNT"));

								dhInsert.setBigDecimal(9, (BigDecimal) header.get("RCP_AMOUNT"));
								dhInsert.setBigDecimal(10, (BigDecimal) header.get("ACCT_AMOUNT"));

								dhInsert.setDate(11, getDate(header, "CW_TIME"));
								dhInsert.setString(12, (String) header.get("CW_STATUS"));
								dhInsert.setString(13, (String) header.get("CW_BY"));
								dhInsert.setString(14, (String) header.get("EBS_FLAG"));

								dhInsert.setString(15, (String) header.get("FIRST_REMINDER_FLAG"));
								dhInsert.setDate(16, getDate(header, "FIRST_REMINDER_DATE"));
								dhInsert.setString(17, (String) header.get("SECOND_REMINDER_FLAG"));
								dhInsert.setDate(18, getDate(header, "SECOND_REMINDER_DATE"));

								dhInsert.setString(19, cwRemark);
								dhInsert.setDate(20, getDate(header, "DUE_DATE"));
								dhInsert.setString(21, concat(header, "CO_NAME1", "CO_NAME2"));
								dhInsert.setString(22, concat(header, "BILL_NAME1", "BILL_NAME2"));

								try {
									dhInsert.execute();
								} catch (Exception e) {
									System.err.println("invo no "+invoiceNo + " " + suf + " " + applNo + " " + cwRemark + " " + header);
									throw e;
								}
								dhCount++;

								List<Map<String, Object>> items = itemMap.get(invoiceNo);
								if (items != null) {
									for (Map<String, Object> item : items) {
										itemIns.setString(1, (String) item.get("ADHOC_INVOICE_TEXT"));
										itemIns.setBigDecimal(2, (BigDecimal) item.get("AMOUNT"));
										itemIns.setString(3, (String) item.get("RM_APPL_NO"));
										itemIns.setString(4, (String) item.get("RM_APPL_NO_SUF"));
										itemIns.setBigDecimal(5, (BigDecimal) item.get("CHARGED_UNITS"));
										itemIns.setString(6, (String) item.get("CHG_INDICATOR"));
										itemIns.setString(7, invoiceNo.toString());
										itemIns.setString(8, (String) item.get("FC_FEE_CODE"));
										itemIns.setDate(9, getDate(item, "GENERATION_TIME"));
										itemIns.setBigDecimal(10, (BigDecimal) item.get("CHARGED_UNITS"));
										itemIns.setString(11, (String) item.get("USER_ID"));
										itemIns.execute();
										itemCount ++;
									}
								}
								List<Map<String, Object>> receipts = receiptMap.get(invoiceNo);
								if (receipts != null) {
									for (Map<String, Object> receipt : receipts) {
										receiptIns.setString(1, (String) receipt.get("ACCOUNTED"));
										receiptIns.setBigDecimal(2, (BigDecimal) receipt.get("AMOUNT"));
										receiptIns.setString(3, (String) receipt.get("INV_RM_APPL_NO"));
										receiptIns.setString(4, (String) receipt.get("INV_RM_APPL_NO_SUF"));
										receiptIns.setDate(5, getDate(receipt, "BATCH_NO"));
										receiptIns.setString(6, (String) receipt.get("CAN_ADJ_BY"));
										receiptIns.setString(7, (String) receipt.get("CAN_ADJ_REMARK"));
										receiptIns.setString(8, (String) receipt.get("CAN_ADJ_STATUS"));
										receiptIns.setDate(9, getDate(receipt, "CAN_ADJ_TIME"));
										receiptIns.setString(10, invoiceNo.toString());
										receiptIns.setDate(11, getDate(receipt, "INPUT_TIME"));
										receiptIns.setString(12, (String) receipt.get("RECEIPT_NO"));
										receiptIns.execute();
										recCount ++;
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("dhCount "+dhCount);
		System.out.println("itemCount "+itemCount);
		System.out.println("recCount "+recCount);
	}

	public void updatePaymentStatus() {
		/*
		 *
update DEMAND_NOTE_HEADERS set PAYMENT_STATUS = '2' where CREATE_BY = 'DM' and DEMAND_NOTE_NO in (select h.DEMAND_NOTE_NO
from DEMAND_NOTE_HEADERS h inner join (select DN_DEMAND_NOTE_NO DEMAND_NOTE_NO, sum(AMOUNT) AMOUNT from DEMAND_NOTE_RECEIPTS group by DN_DEMAND_NOTE_NO) r
on h.DEMAND_NOTE_NO = r.DEMAND_NOTE_NO
where h.AMOUNT > r.AMOUNT);

update DEMAND_NOTE_HEADERS set PAYMENT_STATUS = '1' where CREATE_BY = 'DM' and DEMAND_NOTE_NO in (select h.DEMAND_NOTE_NO
from DEMAND_NOTE_HEADERS h inner join (select DN_DEMAND_NOTE_NO DEMAND_NOTE_NO, sum(AMOUNT) AMOUNT from DEMAND_NOTE_RECEIPTS group by DN_DEMAND_NOTE_NO) r
on h.DEMAND_NOTE_NO = r.DEMAND_NOTE_NO
where h.AMOUNT = r.AMOUNT);

update DEMAND_NOTE_HEADERS set PAYMENT_STATUS = '3' where CREATE_BY = 'DM' and DEMAND_NOTE_NO in (select h.DEMAND_NOTE_NO
from DEMAND_NOTE_HEADERS h inner join (select DN_DEMAND_NOTE_NO DEMAND_NOTE_NO, sum(AMOUNT) AMOUNT from DEMAND_NOTE_RECEIPTS group by DN_DEMAND_NOTE_NO) r
on h.DEMAND_NOTE_NO = r.DEMAND_NOTE_NO
where h.AMOUNT < r.AMOUNT); -- overpaid

update DEMAND_NOTE_HEADERS set PAYMENT_STATUS = '0' where CREATE_BY = 'DM' and DEMAND_NOTE_NO not in (select h.DEMAND_NOTE_NO
from DEMAND_NOTE_HEADERS h inner join (select DN_DEMAND_NOTE_NO DEMAND_NOTE_NO, sum(AMOUNT) AMOUNT from DEMAND_NOTE_RECEIPTS group by DN_DEMAND_NOTE_NO) r
on h.DEMAND_NOTE_NO = r.DEMAND_NOTE_NO);

update  DEMAND_NOTE_HEADERS  set DEMAND_NOTE_STATUS = '3' where CREATE_BY = 'DM';


		 */

	}

	private String concat(Map<String, Object> header, String string, String string2) {
		String s1 = (String) header.get(string);
		String s2 = (String) header.get(string2);
		if (s1 != null && s2 != null) {
			return s1 + " " + s1;
		} else if (s1 != null) {
			return s1;
		} else if (s2 != null) {
			return s2;
		} else {
			return null;
		}
	}

	private java.sql.Date getDate(Map<String, Object> header, String name) {
		Date cwTime = (Date) header.get(name);
		java.sql.Date sqlDate = (cwTime != null) ? new java.sql.Date(cwTime.getTime()) : null;
		return sqlDate;
	}


}
