package com.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

@SupportedAnnotationTypes("com.test.Annotation")
public class Processor extends AbstractProcessor {
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment rndEnv) {
		Messager log = processingEnv.getMessager();
		if (!rndEnv.processingOver()) {
			TypeElement typeElement = processingEnv.getElementUtils().getTypeElement("com.test.Annotation");
			for (@SuppressWarnings("unused")
			Element element : rndEnv.getElementsAnnotatedWith(typeElement)) {
				try {
					JavaFileObject sourceFile = writeSourceFile();
					log.printMessage(Diagnostic.Kind.NOTE, "Source: " + sourceFile.getName());
					log.printMessage(Diagnostic.Kind.NOTE, "Options: " + processingEnv.getOptions().toString());
					boolean success = compile(sourceFile, processingEnv.getOptions());
					log.printMessage(Diagnostic.Kind.NOTE, "Compiled: " + success);
				} catch (IOException e) {
					log.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	private JavaFileObject writeSourceFile() throws IOException {
		JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile("com.test.Test");
		PrintWriter pw = new PrintWriter(sourceFile.openOutputStream());
		pw.println("package com.test;");
		pw.write("class Test { Annotated annotated() { return null; } }");
		pw.close();
		return sourceFile;
	}

	private Boolean compile(JavaFileObject sourceFile, Map<String, String> opts) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		List<String> options = opts.entrySet().stream().filter((e) -> !e.getKey().equals("phase"))
				.flatMap((e) -> Arrays.asList(e.getKey(), e.getValue()).stream()).collect(Collectors.toList());
		Iterable<? extends JavaFileObject> objects = fileManager
				.getJavaFileObjectsFromFiles(Arrays.asList(new File(sourceFile.toUri())));
		Boolean success = compiler.getTask(null, fileManager, null, options, null, objects).call();
		fileManager.close();
		return success;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}
}