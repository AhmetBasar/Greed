package chess.engine.test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeReviewTest {
	
	public static void main(String[] args) {
		URL a = CodeReviewTest.class.getClassLoader().getResource(".");
		File binaryFile = new File(a.getPath());
		File projectFile = binaryFile.getParentFile();
		List<File> javaFiles = new ArrayList<File>();
		retrieveJavaFiles(projectFile, javaFiles);
		
		String projectName = projectFile.getName();
		System.out.println("projectName = " + projectName);
		
		int counter = 0;
		for (File javaFile : javaFiles) {
			String javaFilePath = javaFile.getAbsolutePath();
			javaFilePath = javaFilePath.substring(javaFilePath.lastIndexOf(projectName) + projectName.length() + 1, javaFilePath.length());
			javaFilePath = javaFilePath.replace("\\", ".");
			String fullyQualifiedClassName = javaFilePath.substring(0, javaFilePath.lastIndexOf(".java"));
			
			if (fullyQualifiedClassName.startsWith("src.resources.")) {
				fullyQualifiedClassName = fullyQualifiedClassName.substring("src.resources.".length());
			} else if (fullyQualifiedClassName.startsWith("src.bot.")) {
				fullyQualifiedClassName = fullyQualifiedClassName.substring("src.bot.".length());
			} else if (fullyQualifiedClassName.startsWith("src.ui.")) {
				fullyQualifiedClassName = fullyQualifiedClassName.substring("src.ui.".length());
			} else if (fullyQualifiedClassName.startsWith("test.")) {
				fullyQualifiedClassName = fullyQualifiedClassName.substring("test.".length());
			} else if (fullyQualifiedClassName.startsWith("src.")) {
				fullyQualifiedClassName = fullyQualifiedClassName.substring("src.".length());
			}
			
//			System.out.println(fullyQualifiedClassName);
			
			try {
				Class<?> clazz = Class.forName(fullyQualifiedClassName);
				Field[] declaredFields = clazz.getDeclaredFields();
				for (Field field : declaredFields) {
					boolean isPrimitive = field.getType().isPrimitive() || field.getType().equals(java.lang.String.class);
					boolean isFinal = Modifier.isFinal(field.getModifiers());
					if (Modifier.isStatic(field.getModifiers()) && (!isPrimitive || !isFinal)
							&& !whiteListFields.contains(field.getDeclaringClass().getCanonicalName() + "." + field.getName())
							&& !whiteListClasses.contains(field.getDeclaringClass().getCanonicalName())) {
						System.out.println(field);
						counter++;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		System.out.println("counter = " + counter);
	}
	
	private static void retrieveJavaFiles(File dir, List<File> lst) {
		File[] files = dir.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isDirectory()) {
					retrieveJavaFiles(file, lst);
				} else {
					if (file.getAbsolutePath().endsWith(".java")) {
						lst.add(file);
					}
				} 
			}
		}
	}
	
	private static final Set<String> whiteListFields = new HashSet<String>();
	private static final Set<String> whiteListClasses = new HashSet<String>();
	static {
		
		whiteListClasses.add("chess.bot.BotConstants");
		whiteListClasses.add("chess.bot.image.ImageType");
		
		
//		whiteListFields.add("chess.bot.BoardInteractionManager.fast");
		
	}

}
