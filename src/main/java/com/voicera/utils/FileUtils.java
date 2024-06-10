package com.voicera.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class FileUtils {
	private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);


	private File getFileResource(String fileName) {
		if (fileName == null || fileName.isEmpty())
			return null;
		try {
			return ResourceUtils.getFile("classpath:static/" + fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getStringFromFile(String fileName) {
		if (fileName != null && !fileName.isEmpty()) {
			try {
				File file = getFileResource(fileName);
				if (file != null) {
					LOG.info("Loaded file ...{}", file.getPath());
					Path path = Paths.get(file.getPath());
					return Files.readString(path);
				}
				return "";
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		} else
			return "";
	}

	public void downloadFile(HttpServletResponse response, String filePath) {
		try {
			File res = getFileResource(filePath);
			if (res != null) {
				File file = new File(res.getPath());
				if (file.exists()) {
					String mimeType = URLConnection.guessContentTypeFromName(file.getName());
					if (mimeType == null) {
						mimeType = "application/octet-stream";
					}
					response.setContentType(mimeType);
					response.setHeader("Content-Disposition",
							String.format("inline; filename=\"" + file.getName() + "\""));
					// Here we have mentioned it to show as attachment
					// response.setHeader("Content-Disposition", String.format("attachment;
					response.setContentLength((int) file.length());
					try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file));) {
						FileCopyUtils.copy(inputStream, response.getOutputStream());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
