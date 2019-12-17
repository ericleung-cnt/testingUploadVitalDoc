package org.mardep.ssrs.vitaldoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import migration.SeafarerImages;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class ImageMigration {

	@Autowired
	IVitalDocClient vd;

	/**
	 * read seafarer images from oracle
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@Test
	public void read() throws SQLException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		String driverClass = "oracle.jdbc.driver.OracleDriver";
		String orclUrl = "jdbc:oracle:thin:@10.37.108.131:1521:srisuat";
		String orclUser = "sris";
		String orclPwd = "srissris";
		new SeafarerImages(vd).read(driverClass, orclUrl, orclUser, orclPwd);

	}
	/**
	 * read picture from vitaldoc and write local file
	 * @throws IOException
	 */
	@Test
	public void testDownload() throws IOException {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("SERB number", "SERB_" + "041132");
		parameter.put("Type", "Fingerprint-left");
		byte[] download = vd.download("MMO-Seafarer Image", parameter, true);
		assert download.length > 0;
		try (OutputStream output = new FileOutputStream("d:\\desktop\\fp.bmp")) {
			IOUtils.write(download, output);
		}
	}

	/**
	 * read from a bmp file to write to vitaldoc
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testConvert() throws FileNotFoundException, IOException {
		String[] list = new File("out").list();
		for (String path : list) {
//			String path = "out\\041132_left.bmp";
			String[] tokens = path.substring(path.lastIndexOf('\\') + 1).split("[_\\.]"); // [041132, left, bmp]
			boolean valid = tokens.length == 3 && Arrays.asList("left", "right").contains(tokens[1])
					&& "bmp".equals(tokens[2]);
			if (valid) {
				String serb = tokens[0];
				int type = "left".equals(tokens[1]) ? IVitalDocClient.INDEX_LEFT : IVitalDocClient.INDEX_RIGHT;

				try (FileInputStream in = new FileInputStream(path)) {
					SeafarerImages seafarerImages = new SeafarerImages(vd);
					byte[] content = seafarerImages.bmpToPixel(in);
					seafarerImages.replace(serb, type, content);
				}
			} else {
				throw new IllegalArgumentException("check path " + path);
			}
		}
	}



}
