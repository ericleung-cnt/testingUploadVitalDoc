package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.dao.dn.IDemandNoteReceiptDao;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.domain.dn.DemandNoteReceipt;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.bean.ReceiptCollected01;
import org.mardep.ssrs.report.bean.ReceiptCollected02;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * SSRS Receipt Collected Report
 *
 */
@Service("RPT_FIN_006")
public class RPT_FIN_006 extends AbstractFinReport {

	final static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private IDemandNoteReceiptDao demandNoteReceiptDao;

	@Autowired
	private IDemandNoteItemDao demandNoteItemDao;

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		final String reportTitle="Receipt Collected Report";
		final String reportSubTitle="Period: %s To %s";
		Date from = (Date)inputParam.get(DATE_FROM);
		Date to = (Date)inputParam.get(DATE_TO);

		inputParam.put(REPORT_ID, "FIN006");
		inputParam.put(USER_ID, UserContextThreadLocalHolder.getCurrentUserId());
		inputParam.put(REPORT_TITLE, reportTitle);
		inputParam.put(REPORT_SUB_TITLE, String.format(reportSubTitle, df.format(from), df.format(to)));

		fillData01(inputParam);
		fillData02(inputParam);

		List<String> dummyList = new ArrayList<String>();
		dummyList.add("TEST");

		return generate(dummyList, inputParam);
	}

	private void fillData01(Map<String, Object> inputParam){
		//TODO retrieve data from DB
		Date from = (Date)inputParam.get(DATE_FROM);
		Date to = (Date)inputParam.get(DATE_TO);
		to = new Date(to.getTime() + 24L*3600 * 1000 - 1);

		String sortBy = (String)inputParam.get(SORT_BY);

		List<DemandNoteReceipt> collecteds = demandNoteReceiptDao.findCollected(from, to, sortBy);

		List<ReceiptCollected01> list1 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list2 = new ArrayList<ReceiptCollected01>();
		List<ReceiptCollected01> list3 = new ArrayList<ReceiptCollected01>();
		String lastDemandNoteNo = null;
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int count4 = 0;
		BigDecimal cancel1 = BigDecimal.ZERO;
		BigDecimal cancel2 = BigDecimal.ZERO;
		BigDecimal cancel3 = BigDecimal.ZERO;
		BigDecimal cancel4 = BigDecimal.ZERO;
		BigDecimal recd1 = BigDecimal.ZERO;
		BigDecimal recd2 = BigDecimal.ZERO;
		BigDecimal recd3 = BigDecimal.ZERO;
		BigDecimal recd4 = BigDecimal.ZERO;

		for(DemandNoteReceipt r:collecteds){
			ReceiptCollected01 rc01 = new ReceiptCollected01();
			DemandNoteHeader demandNote = r.getDemandNoteHeader();
			String demandNoteNo = r.getDemandNoteNo();
			if(!demandNoteNo.equalsIgnoreCase(lastDemandNoteNo)){
				lastDemandNoteNo = demandNoteNo;
				//make it blank if same demandNoteNo
				rc01.setDemandNoteNo(demandNoteNo);
				rc01.setAmount(demandNote.getAmount());
				rc01.setBillName(demandNote.getBillName());
			}
			Date issueDate=demandNote.getGenerationTime();
			if(issueDate!=null){
				rc01.setIssueDate(REPORT_DATE_FORMAT.format(issueDate));
			}
			Date dueDate=demandNote.getDueDate();
			if(dueDate!=null){
				rc01.setDueDate(REPORT_DATE_FORMAT.format(dueDate));
			}
			rc01.setDemandNoteStatus(demandNote.getStatusStr());
			rc01.setReceiptStatus(demandNote.getPaymentStatusStr());
			rc01.setReceiptDate(REPORT_DATE_FORMAT.format(r.getInputTime()));
			rc01.setReceiptNo(r.getReceiptNo());
			BigDecimal receiptAmount = r.getAmount();
			//if(DemandNoteHeader.STATUS_CANCELLED.equals(demandNote.getStatus())){
			if (r.getCancelDate()!=null) {
				cancel4 = cancel4.add(receiptAmount);
				if (demandNote.getEbsFlag() == null) {
					cancel1 = cancel1.add(receiptAmount);
				} else if ("1".equals(demandNote.getEbsFlag())) {
					cancel2 = cancel2.add(receiptAmount);
				} else {
					cancel3 = cancel3.add(receiptAmount);
				}
			}else{
				recd4 = recd4.add(receiptAmount);
				if (demandNote.getEbsFlag() == null) {
					recd1 = recd1.add(receiptAmount);
				} else if ("1".equals(demandNote.getEbsFlag())) {
					recd2 = recd2.add(receiptAmount);
				} else {
					recd3 = recd3.add(receiptAmount);
				}
			}
			rc01.setReceiptAmount(r.getAmount());
			//rc01.setIsReceiptCancelled("N");//TODO
			if (r.getCancelDate()!=null) {
				rc01.setIsReceiptCancelled("Y");
			} else {
				rc01.setIsReceiptCancelled("N");
				count4++;
				if (demandNote.getEbsFlag() == null) {
					count1++;
				} else if ("1".equals(demandNote.getEbsFlag())) {
					count2++;
				} else {
					count3++;
				}
			}
			List<DemandNoteItem> items = demandNoteItemDao.findByDemandNoteNo(demandNoteNo);
			//TODO just show the first one ?
			if(items!=null && items.size()>0){
				FeeCode fc = items.get(0).getFeeCode();
				if(fc!=null){
					rc01.setItemCode(fc.getId());
					rc01.setFeeCode(fc.getFormCode());
				}
			}
			if (demandNote.getEbsFlag() == null) {
				list1.add(rc01);
				rc01.setCount(BigDecimal.valueOf(count1));
				rc01.setCancelledAmount(cancel1);
				rc01.setReceivedAmount(recd1);
			} else if ("1".equals(demandNote.getEbsFlag())) {
				list2.add(rc01);
				rc01.setCount(BigDecimal.valueOf(count2));
				rc01.setCancelledAmount(cancel2);
				rc01.setReceivedAmount(recd2);
			} else {
				list3.add(rc01);
				rc01.setCount(BigDecimal.valueOf(count3));
				rc01.setCancelledAmount(cancel3);
				rc01.setReceivedAmount(recd3);
				rc01.setTitle("eBS NON-AUTOPAY");
			}
		}
		inputParam.put("noOfRecords", count4);
		inputParam.put("receivedAmount", recd4);
		inputParam.put("cancelledAmount", cancel4);

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

		JasperReport subreport1 = jasperReportService.getJasperReport("FIN_ReceiptCollected_01.jrxml");
		inputParam.put("SUBREPORT_1", subreport1);
		inputParam.put("SUBREPORTDS_1", list1);
		inputParam.put("SUBREPORT_2", subreport1);
		inputParam.put("SUBREPORTDS_2", list2);
		inputParam.put("SUBREPORT_3", subreport1);
		inputParam.put("SUBREPORTDS_3", list3);
	}

	private void fillData02(Map<String, Object> inputParam){
		Date from = (Date)inputParam.get(DATE_FROM);
		Date to = (Date)inputParam.get(DATE_TO);
		List<DemandNoteItem> collecteds = demandNoteItemDao.findCollected(from, to);
		Map<String, ReceiptCollected02> map = new HashMap<String, ReceiptCollected02>();
		for(DemandNoteItem item:collecteds){
			String feeCode = item.getFcFeeCode();
			logger.info("###########Fee Item Code:{}", feeCode);
			if(!map.containsKey(feeCode)){
				ReceiptCollected02 rc02 = new ReceiptCollected02();
				rc02.setItemCode(feeCode);
				rc02.setFeeCode(item.getFeeCode().getFormCode());
				rc02.setDescription(item.getFeeCode().getEngDesc());
				rc02.setPrice(item.getFeeCode().getFeePrice());
				rc02.setChargedUnit(0);
				rc02.setAmount(BigDecimal.ZERO);
				map.put(feeCode, rc02);
			}
			ReceiptCollected02 rc02 = map.get(feeCode);
			rc02.setChargedUnit(rc02.getChargedUnit() + item.getChargedUnits());
			rc02.setAmount(rc02.getAmount().add(item.getAmount()));
		}
		JasperReport subreport2 = jasperReportService.getJasperReport("FIN_ReceiptCollected_02.jrxml");
		inputParam.put("SUBREPORT_4", subreport2);
		List<ReceiptCollected02> list = new ArrayList<ReceiptCollected02>(map.values());
		Collections.sort(list);
		inputParam.put("SUBREPORTDS_4", list);
	}
}
