package migration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CodeTable {

	private Connection oracle;
	private PrintStream out;

	public CodeTable() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, FileNotFoundException {
		String driverClass = "oracle.jdbc.driver.OracleDriver";
		String orclUrl = "jdbc:oracle:thin:@10.37.108.131:1521:srisuat";
		String orclUser = "sris";
		String orclPwd = "srissris";
		orclUrl = "jdbc:oracle:thin:@10.37.47.101:1522:srisprod";
		orclPwd = "sris2119";
		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
		oracle = DriverManager.getConnection(orclUrl, orclUser, orclPwd);
		out = new PrintStream("./target/codes.sql");
		"a\nb".replaceAll("\n", "'+CHAR(13)+CHAR(10)+'");
	}

	public void readOralce(String tableName, String targetCols, String srcValues)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		System.out.println("INFO:"+tableName);
		out.println("\n-- "  + tableName);
		String targetName = tableName.split("\\s")[0];
		int index = tableName.indexOf("totable ");
		if (index != -1) {
			tableName += " ";
			targetName = tableName.substring(index + "totable ".length(), tableName.indexOf(' ', index + "totable ".length()));
		}
		if (tableName.contains(" identity_insert")) {
			out.println("set identity_insert "  + targetName + " on;");
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try (PreparedStatement statement = oracle.prepareStatement("select " + srcValues + " from " + tableName)) {
			try (ResultSet rs = statement.executeQuery()) {
				ResultSetMetaData metaData = rs.getMetaData();
				String columnName = "";
				for (int i = 0; i < metaData.getColumnCount(); i++) {
					columnName += metaData.getColumnName(i + 1) + ",";
				}
				columnName = columnName.substring(0, columnName.length() - 1);
				String[] cols = targetCols.split(",");
				// out.println(columnName);
				String into = targetName;
				while (rs.next()) {
					String values = "";
//					Map<String, Object> rowMap = new HashMap<>();
					for (int i = 0; i < metaData.getColumnCount(); i++) {
						Object v = rs.getObject(i + 1);
						if (v instanceof String) {
							String replaced = ((String) v).replaceAll("'", "''").trim();
							replaced = replaced.replace("\\", "\\\\");;
							replaced = replaced.replaceAll("\\n", "'+CHAR(13)+CHAR(10)+'");;
							v = "'" + replaced + "'";
						} else if (v instanceof Date) {
							v = "convert(datetime2, '" + df.format(v) + "', 20)";
						}
						values += v + ",";
//						rowMap.put(metaData.getColumnName(i + 1), v);
					}
					values = values.substring(0, values.length() - 1);
					out.println("insert into " + into + "(" + targetCols + ") values (" + values + ");");
				}
			}
		}
		if (tableName.contains(" identity_insert")) {
			out.println("set identity_insert "  + targetName + " off;");
		}
	}

	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		CodeTable codeTable = new CodeTable();
		try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(CodeTable.class.getResourceAsStream("/code_tables.txt")))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("//") || line.startsWith("#")) {
					continue;
				}
				codeTable.readOralce(line, reader.readLine(), reader.readLine());
			}
		}
		codeTable.out.flush();
		codeTable.out.close();
//		codeTable.readOralce("ANNUAL_TON_FEES",
//				"ATF_TON_LO, ATF_TON_HI, ATF_FEE, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION",
//				"ATF_TON_LO, ATF_TON_HI, ATF_FEE, 'DM', '2019-09-21', 'DM', '2019-09-21', 0");
//		codeTable.readOralce("CLASS_SOCIETIES",
//				"CLASS_SOC_CODE, CLASS_SOC_NAME, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION",
//				"CLASS_SOC_CODE, CLASS_SOC_NAME, 'DM', '2019-09-21', 'DM', '2019-09-21', 0");
//		codeTable.readOralce("CLINICS",
//				"CLASS_SOC_CODE, CLASS_SOC_NAME, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION",
//				"CLASS_SOC_CODE, CLASS_SOC_NAME, 'DM', '2019-09-21', 'DM', '2019-09-21', 0");

	}
}
