package org.mardep.ssrs.dao.sr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionJpaDao extends AbstractJpaDao<Transaction, Long> implements ITransactionDao {

	@Override
	public Transaction save(String applNo, String code, Transaction tx) {
		if (applNo == null) {
			applNo = tx.getApplNo();
		}
		PagingCriteria pagingCriteria = new PagingCriteria(0, 1);
		List<Transaction> resultList = findTransactions(applNo, pagingCriteria);
		if (!resultList.isEmpty()) {

			try {
				String timeStr = ("0000"+resultList.get(0).getHourChange());
				timeStr = timeStr.substring(timeStr.length() - 4);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				Date latest = sdf.parse(sdf.format(resultList.get(0).getDateChange()).substring(0,8) + timeStr);
System.out.println("latest date:" + latest);
				Date date = getDateChange(tx);
System.out.println("date change:"+ date);
				if (date.before(latest)) {
					throw new IllegalArgumentException("Please check latest transaction date change, " + latest);
				}
			} catch (ParseException e) {
				throw new IllegalStateException(e);
			}
		}

		if (applNo != null) {
			tx.setApplNo(applNo);
		}
		tx.setCode(code);
		tx.setUserId(UserContextThreadLocalHolder.getCurrentUserId());
		tx.setTransactionTime(new Date());
		return save(tx);
	}

	private Date getDateChange(Transaction tx) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String timeStr;
		timeStr = ("0000"+tx.getHourChange());
		timeStr = timeStr.substring(timeStr.length() - 4);
		String txDateChangeStr = sdf.format(tx.getDateChange()).substring(0,8) + timeStr;
		Date date = sdf.parse(txDateChangeStr);
		return date;
	}

	private List<Transaction> findTransactions(String applNo, PagingCriteria pagingCriteria) {
		List<Transaction> resultList = new ArrayList<>();
		Map<String, Criteria> map = new HashMap<String, Criteria>();
		map.put("applNo", new Criteria("applNo", applNo));
		findByPaging(map, new SortByCriteria(Arrays.asList("-dateChange","-hourChange")), pagingCriteria, resultList);
		return resultList;
	}

	@Override
	public Transaction save(Transaction entity) {
		if (entity.getVersion() != null) { // updating
			List<Transaction> list = findTransactions(entity.getApplNo(), null);
			for ( int i = 0; i < list.size(); i++) {
				if (list.get(i).getTransactionTime().getTime()== entity.getTransactionTime().getTime() &&
						list.get(i).getCode().equals(entity.getCode())) {
					try {
						Date dateChange = getDateChange(entity);
						if (i > 0) {
							Date next = getDateChange(list.get(i - 1));
							if (next.before(dateChange)) {
								throw new IllegalArgumentException("Please check transaction date change, " + dateChange);
							}
						}
						if (i < list.size() - 1) {
							Date last = getDateChange(list.get(i + 1));
							if (last.after(dateChange)) {
								throw new IllegalArgumentException("Please check transaction date change" + dateChange );
							}
						}
						break;
					} catch (ParseException e) {
						throw new IllegalStateException(e);
					}
				}
			}
		}
		return super.save(entity);
	}

}
