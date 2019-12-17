package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.mardep.ssrs.service.IDemandNoteService;
import org.mardep.ssrs.service.IMortgageService;
import org.mardep.ssrs.service.IShipRegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;

@Service("CoR")
public class CertificateOfRegistry extends AbstractSrReport {

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

	public CertificateOfRegistry() {
		super("CoR.jrxml", null);
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		boolean crosscheck = Boolean.TRUE.equals(inputParam.get("crosscheck"));
		if (Boolean.TRUE.equals(inputParam.get("paymentRequired"))) {
			FeeCode code = fcDao.findById("11");
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
		if (reportDate == null) {
			reportDate = new Date();
		}
		String applNo = (String) inputParam.get("applNo");
		logger.info("Report Date:{}", reportDate);

		Map<String, Object> params = new HashMap<String, Object>();
		String formatted = createDateFormat().format(reportDate);
		params.put("reportDate", formatted);
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		params.put("userId", currentUser);
		params.put("SUBREPORT_2", jasperReportService.getJasperReport("RPT-SR-011-subreport02.jrxml"));
		params.put("printMortgage", Boolean.TRUE);
		params.put("SUBREPORT_1", jasperReportService.getJasperReport("RPT-SR-011-subreport01.jrxml"));
		params.put("issueTime", formatted);
		params.put("issueTimeChi", formatted);
		if (!crosscheck) {
			Long registrarId = (Long) inputParam.get("registrar");
			if (registrarId != null) {
				Registrar registrar = rDao.findById(registrarId);
				params.put("registrar", registrar.getName());
			} else {
				params.put("registrar", "");
			}
		}

		return getPdf(reportDate, params, applNo, crosscheck);
	}

	private byte[] getPdf(Date reportDate, Map<String, Object> params, String applNo, boolean crosscheck)
			throws JRException {
		HashMap<Object, Object> reportContent = getReportContent(applNo, reportDate, crosscheck);
		return jasperReportService.generateReport(getReportFileName(), Arrays.asList(reportContent), params);
	}

	private HashMap<Object, Object> getReportContent(String applNo, Date reportDate, boolean crosscheck) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();
		if (dateFormat.format(reportDate).equals(dateFormat.format(now))) {
			reportDate = now;
		}

		//RegMaster regM = crosscheck ? service.findById(RegMaster.class, applNo) : service.findById(applNo, reportDate);
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
		//20190806 set default to N/A when how propelled is null
		regMaster.put("howPropelled", 
				regM.getHowProp()!=null ? regM.getHowProp() : "N/A");
		regMaster.put("netTonnage", new DecimalFormat("#,### tons").format(isNull(regM.getRegNetTon(), BigDecimal.ZERO)));
		regMaster.put("noOfSetsEngine", 
				regM.getEngSetNum()!=null ? String.valueOf(regM.getEngSetNum()) : "0");
		//20190806 set default to N/A when totalEnginePower is null
		regMaster.put("totalEnginePower", 
				regM.getEngPower()!=null ? regM.getEngPower() : "N/A");

		regMaster.put("portOfReg", "HONG KONG");
		regMaster.put("portOfRegChi", "\u9999\u6e2f");

		regMaster.put("dateKeelLaid", regM.getBuildDate());
		List<BuilderMaker> blders;
		if (crosscheck) {
			BuilderMaker bmCriteria = new BuilderMaker();
			bmCriteria.setApplNo(applNo);
			blders = bmDao .findByCriteria(bmCriteria);
		} else {
			//blders = service.findBuilders(applNo, reportDate);
			blders = service.findBuildersByApplId(applNo);
		}
		if (!blders.isEmpty()) {
			BuilderMaker builder = blders.get(0);
			regMaster.put("builder", builder);
		}

		// 20190806 set default to N/A if mainEngineType is null
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
		
		if (regM.getIntTot()!=null) {
			String totalInterest = String.valueOf(regM.getIntTot()) +
			("S".equals(regM.getIntUnit()) ? " Shares" :
				"%".equals(regM.getIntUnit()) ? " Percentage" :
					"P".equals(regM.getIntUnit()) ? " Parts" :
						"R".equals(regM.getIntUnit()) ? " Fractions" : "");
			regMaster.put("intTot", totalInterest);
		}

		// 20190807 set report date not less than reg date
		Date certIssueDate = reportDate;
		if (certIssueDate.compareTo(regM.getRegDate())<0) {
			certIssueDate = regM.getRegDate();
		}
		String formatted = createDateFormat().format(certIssueDate);
		regMaster.put("certIssueDate", formatted);

		Representative rep;
		if (crosscheck) {
			rep = service.findById(Representative.class, applNo);
		} else {
			//rep = service.findRepById(applNo, reportDate);
			rep = service.findRpByApplId(applNo);
		}
		if (rep != null) {
			regMaster.put("repName", rep.getName());
			regMaster.put("repAddress",
					(rep.getAddress1() == null ? "" : rep.getAddress1()) + "\n" +
							(rep.getAddress2() == null ? "" : rep.getAddress2()) + "\n" +
							(rep.getAddress3() == null ? "" : rep.getAddress3()));
		}
		List<Map<String, ?>> dataList = new ArrayList<Map<String, ?>>();
		List<Owner> owners;
		if (crosscheck) {
			Owner criteria = new Owner();
			criteria.setApplNo(applNo);
			owners = service.findByCriteria(criteria);
		} else {
			//owners = service.findOwners(applNo, reportDate);
			owners = service.findOwnersByApplId(applNo);
		}
		regMaster.put("demiseDetails", "");
		regMaster.put("ownerDetails", "");
		for (int i = 0; i < owners.size(); i++) {
			Owner owner = owners.get(i);
			HashMap<String, Object> subreportRow = new HashMap<>();
			subreportRow.put("ownerName", owner.getName());
			subreportRow.put("ownerStatus", owner.getStatus());
			String ownerAddr = (owner.getAddress1() != null ? owner.getAddress1() :"") + "\n" +
					(owner.getAddress2() != null ? owner.getAddress2() :"") + "\n" +
					(owner.getAddress3() != null ? owner.getAddress3() :"");
			subreportRow.put("ownerAddress", ownerAddr);
			subreportRow.put("percentage", owner.getIntMixed() == null ?
					(owner.getIntNumberator() != null ? owner.getIntNumberator() : "-") + "/" +
					(owner.getIntDenominator() != null ? owner.getIntDenominator() : "-") :
						owner.getIntMixed().toString());
			subreportRow.put("placeOfIncorp", owner.getIncorpPlace());

			String ownerStr = "";
			if (owner.getName() != null) {
				ownerStr = owner.getName() + "\n" +
						ownerAddr;
			}

			boolean demise = Owner.TYPE_DEMISE.equals(owner.getType());
			if (demise) {
				regMaster.put("demiseEDate", owner.getCharterEdate());
				regMaster.put("demiseSDate", owner.getCharterSdate());
				regMaster.put("demisePlace", owner.getIncorpPlace());
				regMaster.put("demiseDetails", ownerStr);
			} else {
				regMaster.put("ownerDetails", ownerStr);
				dataList.add(subreportRow);
			}
//			HashMap<String, Object> subreportRow = new HashMap<>();
//			subreportRow.put("ownerName", owner.getName());
//			subreportRow.put("ownerStatus", owner.getStatus());
//			String ownerAddr = (owner.getAddress1() != null ? owner.getAddress1() :"") + "\n" +
//					(owner.getAddress2() != null ? owner.getAddress2() :"") + "\n" +
//					(owner.getAddress3() != null ? owner.getAddress3() :"");
//			subreportRow.put("ownerAddress", ownerAddr);
//			subreportRow.put("percentage", owner.getIntMixed() == null ?
//					(owner.getIntNumberator() != null ? owner.getIntNumberator() : "-") + "/" +
//					(owner.getIntDenominator() != null ? owner.getIntDenominator() : "-") :
//						owner.getIntMixed().toString());
//			subreportRow.put("placeOfIncorp", owner.getIncorpPlace());
//
//			String ownerStr = "";
//			if (owner.getName() != null) {
//				ownerStr = owner.getName() + "\n" +
//						ownerAddr;
//			}
			//regMaster.put(demise ? "demiseDetails" : "ownerDetails", ownerStr);
			//dataList.add(subreportRow);
		}
		regMaster.put("owners", dataList);

		// printMortgage
		List<Map<String, ?>> dataList2 = new ArrayList<Map<String, ?>>();
		List<Mortgage> mortgages;
		if (crosscheck) {
			Mortgage entity = new Mortgage();
			entity.setApplNo(applNo);
			mortgages = service.findByCriteria(entity);
		} else {
			//mortgages = mortgageSrv.findMortgages(applNo, reportDate);
			mortgages = mortgageSrv.findMortgagesByApplId(applNo);
		}
		for (int i = 0; i < mortgages.size(); i++) {
			Mortgage mortgage = mortgages.get(i);
			Date regTime = mortgage.getRegTime();
			if (regTime == null || "D".equals(mortgage.getMortStatus())) {
				continue;
			}

			HashMap<String, Object> subreportRow = new HashMap<>();
			subreportRow.put("mortgageCode", mortgage.getPriorityCode());
			subreportRow.put("priorityCode", mortgage.getPriorityCode());

			String intTot;
			if (crosscheck) {
				intTot = regM.getIntTot() + ("S".equals(regM.getIntUnit()) ? " Shares" :
					"%".equals(regM.getIntUnit()) ? " Percentage" :
						"P".equals(regM.getIntUnit()) ? " Parts" :
							"R".equals(regM.getIntUnit()) ? " Fractions" : "");
			} else {
				intTot = rmDao.getIntTotAt(mortgage.getApplNo(), regTime);
			}
			subreportRow.put("propertyOfShip", intTot);
			List<Mortgagor> mortgagors;
			if (crosscheck) {
				Mortgagor entity = new Mortgagor();
				entity.setApplNo(applNo);
				mortgagors = service.findByCriteria(entity);
			} else {
				//mortgagors = mortgageSrv.findMortgagors(mortgage, reportDate);
				mortgagors = mortgageSrv.findMortgagorsByMortgage(mortgage);
			}
			StringBuilder name = new StringBuilder();
			StringBuilder place = new StringBuilder();
			mortgagors.forEach(m -> {
				for (Owner owner : owners) {
					if (owner.getOwnerSeqNo().equals(m.getSeq())) {
						name.append(owner.getName()).append(", ");
						place.append(owner.getIncorpPlace()).append(", ");
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

			List<Mortgagee> mortgagees;
			if (crosscheck) {
				Mortgagee entity = new Mortgagee();
				entity.setApplNo(applNo);
				mortgagees = service.findByCriteria(entity);
			} else {
				//mortgagees = mortgageSrv.findMortgagees(mortgage, reportDate);
				mortgagees = mortgageSrv.findMortgageesByMortgage(mortgage);
			}
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
			dataList2.add(subreportRow);
		}
		regMaster.put("mortgages", dataList2);
		return regMaster;
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
