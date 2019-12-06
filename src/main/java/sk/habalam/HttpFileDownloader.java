package sk.habalam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class HttpFileDownloader {

	private static final Logger logger = LoggerFactory.getLogger(HttpFileDownloader.class);

	private static final String INPUT_FILE_PATH =
		ClassLoader.getSystemResource("import-data/input_ids.txt").getPath();

	public static void main(String[] args) {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			List<String> ids = Files.readLines(new File(INPUT_FILE_PATH), Charsets.UTF_8);
			ids.forEach(id -> getAndWriteFile(httpClient, id));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void getAndWriteFile(CloseableHttpClient httpClient, String id) {
		HttpGet get = new HttpGet("http://www.registeruz.sk/cruz-public/domain/financialreport/attachment/" + id);
		try (CloseableHttpResponse response = httpClient.execute(get)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String fileName = "output_" + id + ".pdf";
				try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
					entity.writeTo(outputStream);
					logger.info("Writed file - " + fileName);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
