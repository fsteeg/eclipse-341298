# eclipse-341298

Test for http://bugs.eclipse.org/341298

In Eclipse, open the error log view, and import the projects from this repo.

The processor.jar in the annotation project is created by exporting the processor project as a Jar.

When running with javac, we can get the classpath using reflection, in `341298_Annotation` run:

`javac -classpath src -processorpath processor.jar src/com/test/Annotated.java`
