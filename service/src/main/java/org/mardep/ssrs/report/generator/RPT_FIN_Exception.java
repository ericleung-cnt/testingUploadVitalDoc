package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.dn.IDemandNoteReceiptDao;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.bean.ReceiptCollected01;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * SSRS Fin Receipt Exception Report: DemandNote / Receipt
 *  <li>DemandNoteNo
 *  <li>IssueDate
 *  <li>DueDate
 *  <li>Amount
 *  <li>ReceiptNo
 *  <li>ReceiptAmount
 *  <li>ReceiptDate
 *  <li>ReceiptStatus
 *  <li>DemandNoteStatus
 *  <li>Remarks
 *
 */
@Service("RPT_FIN_EXCEPTION")
public class RPT_FIN_Exception extends AbstractFinReport {

	public final static DateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

	@Autowired
	private IDemandNoteReceiptDao demandNoteReceiptDao;

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		final String reportTitle="Exception Report";
		inputParam.put(REPORT_TITLE, reportTitle);
		inputParam.put(REPORT_ID, "FINXXX");
		inputParam.put(USER_ID, UserContextThreadLocalHolder.getCurrentUserName());

		fillData01(inputParam);

		List<String> dummyList = new ArrayList<String>();
		dummyList.add("TEST");

		return generate(dummyList, inputParam);
	}

	private void fillData01(Map<String, Object> inputParam){
		Date from = (Date)inputParam.get(DATE_FROM);
		Date to = (Date)inputParam.get(DATE_TO);
		final String reportSubTitle="From %s To %s";
		inputParam.put(REPORT_SUB_TITLE, String.format(reportSubTitle, TITLE_DATE_FORMAT.format(from), TITLE_DATE_FORMAT.format(to)));
		to = new Date(to.getTime() + 24L*3600 * 1000 - 1);

		List<DemandNoteReceipt> receiptList = demandNoteReceiptDao.findException(from, to); //TODO need to check logic
		receiptList.sort((a,b) -> {
			return a.getDemandNoteNo().compareTo(b.getDemandNoteNo());
		});

		HashSet<Object> demandNoteNos = new HashSet<>();
		List<ReceiptCollected01> list1 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list2 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list3 = new ArrayList<ReceiptCollected01>();
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int count4 = 0;
		BigDecimal recd1 = BigDecimal.ZERO;
		BigDecimal recd2 = BigDecimal.ZERO;
		BigDecimal recd3 = BigDecimal.ZERO;
		BigDecimal recd4 = BigDecimal.ZERO;

		for(DemandNoteReceipt receipt:receiptList){
			DemandNoteHeader demandNote = receipt.getDemandNoteHeader();

			int type;
			if (demandNote.getEbsFlag() == null) {
				type = 1;
			} else if ("1".equals(demandNote.getEbsFlag())) {
				type = 2;
			} else {
				type = 3;
			}

			ReceiptCollected01 er = new ReceiptCollected01();
			if (demandNoteNos.contains(demandNote.getDemandNoteNo())) {
				// skip demand note details
			} else {
				er.setDemandNoteNo(receipt.getDemandNoteNo());
				er.setIssueDate(REPORT_DATE_FORMAT.format(demandNote.getGenerationTime()));
				er.setDueDate(REPORT_DATE_FORMAT.format(demandNote.getDueDate()));
				String status = "";
				if (demandNote.getPaymentStatus() != null) {
					switch (demandNote.getPaymentStatus()) {
					case DemandNoteHeader.PAYMENT_STATUS_OUTSTANDING:
						status = "OUTSTANDING";
						break;
					case DemandNoteHeader.PAYMENT_STATUS_OVERPAID:
						status = "PAID(OVERPAID)";
						break;
					case DemandNoteHeader.PAYMENT_STATUS_PARTIAL:
						status = "OUTSTANDING(PARTIAL)";
						break;
					}
				}
				er.setReceiptStatus(status);
				BigDecimal demandNoteAmount = demandNote.getAmount();
				er.setAmount(demandNoteAmount);
				count4++;
				recd4 = recd4.add(demandNoteAmount);
				switch (type) {
				case 1:
					recd1 = recd1.add(demandNoteAmount);
					count1++;
					break;
				case 2:
					recd2 = recd2.add(demandNoteAmount);
					count2++;
					break;
				case 3:
					recd3 = recd3.add(demandNoteAmount);
					count3++;
					break;
				}

				demandNoteNos.add(demandNote.getDemandNoteNo());
				er.setDemandNoteStatus("REFUND"); //TODO
			}
			er.setReceiptNo(receipt.getReceiptNo());
			BigDecimal receiptAmount = receipt.getAmount();
			er.setReceiptAmount(receiptAmount);
			er.setReceiptDate(REPORT_DATE_FORMAT.format(receipt.getInputTime()));

			er.setRemarks(receipt.getRemark());

			switch (type) {
			case 1:
				er.setCount(BigDecimal.valueOf(count1));
				er.setReceivedAmount(recd1);
				list1.add(er);
				break;
			case 2:
				er.setCount(BigDecimal.valueOf(count2));
				er.setReceivedAmount(recd2);
				list2.add(er);
				break;
			case 3:
				er.setCount(BigDecimal.valueOf(count3));
				er.setReceivedAmount(recd3);
				er.setTitle("eBS NON-AUTOPAY");
				list3.add(er);
				break;
			}
		}
		inputParam.put("demandNoteRetrieved", count4);
		inputParam.put("demandNoteAmount", recd4);

		if (list1.isEmpty()) {
			list1.add(new ReceiptCollected01());
		}
		if (list2.isEmpty()) {
			list2.add(new ReceiptCollected01());
		}
		if (list3.isEmpty()) {
			list3.add(new ReceiptCollected01());
		}

		list1.get(0).setTitle("NON-eBS");
		list2.get(0).setTitle("ebs AUTOPAY");
		list3.get(0).setTitle("eBS NON-AUTOPAY");

		JasperReport subreport1 = jasperReportService.getJasperReport("FIN_Exception_01.jrxml");
		inputParam.put("SUBREPORT_1", subreport1);
		inputParam.put("SUBREPORTDS_1", list1);
		inputParam.put("SUBREPORT_2", subreport1);
		inputParam.put("SUBREPORTDS_2", list2);
		inputParam.put("SUBREPORT_3", subreport1);
		inputParam.put("SUBREPORTDS_3", list3);
	}

}