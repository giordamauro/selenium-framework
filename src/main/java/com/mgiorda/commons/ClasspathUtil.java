package com.mgiorda.commons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

public final class ClasspathUtil {

	private ClasspathUtil() {

	}

	public static File getClasspathFile(String fileProperty) {

		File classpathFile = new File(fileProperty);
		if (!classpathFile.exists()) {

			@SuppressWarnings("resource")
			ApplicationContext appContext = new ClassPathXmlApplicationContext();
			Resource resource = appContext.getResource("classpath:" + fileProperty);

			try {

				classpathFile.getParentFile().mkdirs();
				classpathFile.createNewFile();
				InputStream inputStream = resource.getInputStream();

				FileOutputStream outputStream = new FileOutputStream(classpathFile);

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				outputStream.close();

			} catch (IOException e) {
				throw new IllegalStateException(String.format("Exception getting Classpath file '%s'", fileProperty), e);
			}
		}

		return classpathFile;
	}

}
