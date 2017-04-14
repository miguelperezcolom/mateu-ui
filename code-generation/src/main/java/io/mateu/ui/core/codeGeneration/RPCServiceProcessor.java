package io.mateu.ui.core.codeGeneration;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes({ "io.mateu.ui.core.communication.Service" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RPCServiceProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementsUtils;
    private Types typeUtils;
    private Filer filer;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // reference often used tools from the processingEnv
        messager = processingEnv.getMessager();
        elementsUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();

        messager.printMessage(Kind.WARNING, "Generando fuentes: Inicio");

        messager.printMessage(Kind.NOTE, "filer=" + filer.getClass().getCanonicalName());

        // generate code for annotated elements
        Set<? extends Element> annotatedElements;
        try {
            annotatedElements = roundEnv.getElementsAnnotatedWith((Class<? extends Annotation>) Class.forName("io.mateu.ui.core.communication.Service"));
            for (TypeElement element : ElementFilter.typesIn(annotatedElements)) {
                messager.printMessage(Kind.WARNING, "Generando fuentes para " + element.getQualifiedName());
                generateInterfazAsincrona(messager, elementsUtils, typeUtils, filer, element);
                generateClientSideImpl(element);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // claim the annotation
        return false;
    }

    private void generateClientSideImpl(TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando implementación lado cliente...");
        try {
            String simpleName = clase.getSimpleName().toString();
            String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
            String typeName = clase.getSimpleName().toString() + "ClientSideImpl";

            String pn = packageName;


            String pnc = packageName.replaceAll("\\.shared\\.", ".client.");
            if (pnc.endsWith(".shared")) pnc = pnc.substring(0, pnc.lastIndexOf(".") + 1) + "client";

            String pns = packageName.replaceAll("\\.shared\\.", ".server.");
            if (pns.endsWith(".shared")) pns = pns.substring(0, pns.lastIndexOf(".") + 1) + "server";

            messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

            JavaFileObject javaFile = filer.createSourceFile(pnc + "." + typeName, clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package " + pnc + ";");
            pw.println("");
            pw.println("import io.mateu.ui.core.shared.AsyncCallback;");
            pw.println("import io.mateu.ui.core.client.app.MateuUI;");
            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating a default implementation the");
            pw.println(" * for the class {@link " + clase.getQualifiedName().toString() + "}");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("public class " + typeName + " implements " + pnc + "." + clase.getSimpleName().toString() + "Async {");
            pw.println();

            for (ExecutableElement m : ElementFilter.methodsIn(clase.getEnclosedElements())) {

                pw.println("\t@Override");

                String s = "\tpublic void ";

                s += m.getSimpleName();
                s += "(";
                for (VariableElement p : m.getParameters()) {
                    s += p.asType().toString();
                    s += " ";
                    s += p.getSimpleName();
                    s += ", ";
                }
                s += "AsyncCallback<" + getTipoCallback(m.getReturnType()) + "> callback";
                s += ")";

                s += " {\n\n";

                s += "";

                s += "\t\ttry {\n\n\t\t\t\t";


                if (!TypeKind.VOID.equals(m.getReturnType().getKind())) s += getTipoCallback(m.getReturnType()) + " r = ";
                //s += "new " + pns + "." + simpleName + "Impl().";
                s += "((" + clase.asType().toString() + ")Class.forName(\"" + pns + "." + simpleName + "Impl\").newInstance()).";

                s += m.getSimpleName();
                s += "(";
                int pos = 0;
                for (VariableElement p : m.getParameters()) {
                    if (pos++ > 0) s += ", ";
                    s += p.getSimpleName();
                }
                s += ")";

                s += ";";

                s += "\n\n";

                s += "" +
                        "\n" +
                        "                            callback.onSuccess(" + ((TypeKind.VOID.equals(m.getReturnType().getKind()))?"null":"r") + ");\n" +
                        "\n" +
                        "                        ";

                s += "\n\n\t\t} catch (Throwable e) {\n" +
                        "e.printStackTrace();";

                s += "\n" +
                        "\n" +
                        "                            callback.onFailure(e);\n" +
                        "\n" +
                        "                        ";

                s += "\n\n\t\t}";

                s += "            ";

                pw.println(s);
                pw.println("\n\n\t}");
                pw.println();


            }

            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void generateThreadedClientSideImpl(TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando implementación lado cliente...");
        try {
            String simpleName = clase.getSimpleName().toString();
            String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
            String typeName = clase.getSimpleName().toString() + "ClientSideImpl";

            String pn = packageName;


            String pnc = packageName.replaceAll("\\.shared\\.", ".client.");
            if (pnc.endsWith(".shared")) pnc = pnc.substring(0, pnc.lastIndexOf(".") + 1) + "client";

            String pns = packageName.replaceAll("\\.shared\\.", ".server.");
            if (pns.endsWith(".shared")) pns = pns.substring(0, pns.lastIndexOf(".") + 1) + "server";

            messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

            JavaFileObject javaFile = filer.createSourceFile(pnc + "." + typeName, clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package " + pnc + ";");
            pw.println("");
            pw.println("import io.mateu.ui.core.shared.AsyncCallback;");
            pw.println("import io.mateu.ui.core.client.app.MateuUI;");
            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating a default implementation the");
            pw.println(" * for the class {@link " + clase.getQualifiedName().toString() + "}");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("public class " + typeName + " implements " + pnc + "." + clase.getSimpleName().toString() + "Async {");
            pw.println();

            for (ExecutableElement m : ElementFilter.methodsIn(clase.getEnclosedElements())) {

                pw.println("\t@Override");

                String s = "\tpublic void ";

                s += m.getSimpleName();
                s += "(";
                for (VariableElement p : m.getParameters()) {
                    s += p.asType().toString();
                    s += " ";
                    s += p.getSimpleName();
                    s += ", ";
                }
                s += "AsyncCallback<" + getTipoCallback(m.getReturnType()) + "> callback";
                s += ")";

                s += " {\n\n";

                s += "MateuUI.run(new Runnable() {\n" +
                        "            @Override\n" +
                        "            public void run() {";

                s += "\t\ttry {\n\n\t\t\t\t";


                if (!TypeKind.VOID.equals(m.getReturnType().getKind())) s += getTipoCallback(m.getReturnType()) + " r = ";
                //s += "new " + pns + "." + simpleName + "Impl().";
                s += "((" + clase.asType().toString() + ")Class.forName(\"" + pns + "." + simpleName + "Impl\").newInstance()).";

                s += m.getSimpleName();
                s += "(";
                int pos = 0;
                for (VariableElement p : m.getParameters()) {
                    if (pos++ > 0) s += ", ";
                    s += p.getSimpleName();
                }
                s += ")";

                s += ";";

                s += "\n\n";

                s += "MateuUI.runInUIThread(new Runnable() {\n" +
                        "                        @Override\n" +
                        "                        public void run() {\n" +
                        "\n" +
                        "                            callback.onSuccess(" + ((TypeKind.VOID.equals(m.getReturnType().getKind()))?"null":"r") + ");\n" +
                        "\n" +
                        "                        }\n" +
                        "                    });";

                s += "\n\n\t\t} catch (Exception e) {\n" +
                        "e.printStackTrace();";

                s += "MateuUI.runInUIThread(new Runnable() {\n" +
                        "                        @Override\n" +
                        "                        public void run() {\n" +
                        "\n" +
                        "                            callback.onFailure(e);\n" +
                        "\n" +
                        "                        }\n" +
                        "                    });";

                s += "\n\n\t\t}";

                s += "            }\n" +
                        "        });";

                pw.println(s);
                pw.println("\n\n\t}");
                pw.println();


            }

            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void generateHessian(TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando hessian...");
        try {
            String simpleName = clase.getSimpleName().toString();
            String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
            String typeName = clase.getSimpleName().toString() + "Hessian";

            String pn = packageName.replaceAll("\\.client\\.", ".server.");
            if (pn.endsWith(".client")) pn = pn.substring(0, pn.lastIndexOf(".") + 1) + "server";

            messager.printMessage(Kind.NOTE, "" + packageName + "->" + pn);

            JavaFileObject javaFile = filer.createSourceFile(pn + "." + typeName, clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package " + pn + ";");
            pw.println("");
            pw.println("import com.caucho.hessian.server.HessianServlet;");
            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating an hessian implementation the");
            pw.println(" * for the class {@link " + clase.getQualifiedName().toString() + "}");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("public class " + typeName + " extends HessianServlet implements " + clase.asType().toString() + " {");
            pw.println();


            for (ExecutableElement m : ElementFilter.methodsIn(clase.getEnclosedElements())) {

				/*
				 	@Override
	public AlmacenDatos getDatosCaja(String id) throws Exception {
		return new CarteraServiceImpl().getDatosCaja(id);
	}

				 */

                pw.println("\t@Override");
                String pars = "";
                int pos = 0;
                for (VariableElement p : m.getParameters()) {
                    if (pos++ > 0) pars += ", ";
                    pars += p.asType().toString() + " " + p.getSimpleName();
                }

                String z = "";
                if (m.getThrownTypes().size() > 0) {
                    z += " throws ";
                    pos = 0;
                    for (TypeMirror t : m.getThrownTypes()) {
                        if (pos++ > 0) z += ", ";
                        z += t.toString();
                    }
                }

                pw.println("\tpublic " + ((TypeKind.VOID.equals(m.getReturnType().getKind()))?"void":m.getReturnType().toString()) + " " + m.getSimpleName() + "(" + pars + ") " + z +" {");

                String s = "\t\t";
                if (!TypeKind.VOID.equals(m.getReturnType().getKind())) s += "return ";
                s += "new " + simpleName + "Impl().";

                s += m.getSimpleName();
                s += "(";
                pos = 0;
                for (VariableElement p : m.getParameters()) {
                    if (pos++ > 0) s += ", ";
                    s += p.getSimpleName();
                }
                s += ")";

                s += ";";

                pw.println(s);
                pw.println("\t}");
                pw.println();


            }


            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateInterfazAsincrona(Messager messager, Elements elementsUtils, Types typeUtils, Filer filer, TypeElement clase) {
        messager.printMessage(Kind.NOTE, "generando interfaz asíncrona...");
        try {
            String simpleName = clase.getSimpleName().toString();
            String packageName = elementsUtils.getPackageOf(clase).getQualifiedName().toString();
            String typeName = clase.getSimpleName().toString() + "Async";

            packageName = packageName.replaceAll("\\.shared\\.", ".client.");
            if (packageName.endsWith(".shared")) packageName = packageName.substring(0, packageName.lastIndexOf(".") + 1) + "client";


            JavaFileObject javaFile = filer.createSourceFile(packageName + "." + typeName, clase);
            messager.printMessage(Kind.NOTE, "generando " + javaFile.toUri() + "...");
            Writer writer = javaFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);

            pw.println("package " + packageName + ";");
            pw.println("");
            pw.println("import io.mateu.ui.core.shared.AsyncCallback;");
            pw.println("");
            pw.println("/**");
            pw.println(" * Generated class creating an async interface for the");
            pw.println(" * for the class {@link " + clase.getQualifiedName().toString() + "}");
            pw.println(" * ");
            pw.println(" * @author Miguel");
            pw.println(" */");
            pw.println("public interface " + typeName + " {");
            pw.println();

            for (ExecutableElement m : ElementFilter.methodsIn(clase.getEnclosedElements())) {

                String s = "\tpublic void ";

                s += m.getSimpleName();
                s += "(";
                for (VariableElement p : m.getParameters()) {
                    s += p.asType().toString();
                    s += " ";
                    s += p.getSimpleName();
                    s += ", ";
                }
                s += "AsyncCallback<" + getTipoCallback(m.getReturnType()) + "> callback";
                s += ")";

                s += ";";

                pw.println(s);
                pw.println();


            }


            pw.println("}");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getTipoCallback(TypeMirror typeMirror) {
        TypeKind k = typeMirror.getKind();
        if (TypeKind.VOID.equals(k)) return "Void";
        else if (k.isPrimitive()) {
            if (TypeKind.INT.equals(k)) return "Integer";
            else if (TypeKind.BOOLEAN.equals(k)) return "Boolean";
            else if (TypeKind.DOUBLE.equals(k)) return "Double";
            else return typeMirror.getKind().name();
        } else return typeMirror.toString();
    }

}
