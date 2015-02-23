package de.greenrobot.event.annotationprocessor;

import de.greenrobot.event.Subscribe;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("de.greenrobot.event.Subscribe")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class EventBusAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (annotations.isEmpty()) {
            return false;
        }
        Messager messager = processingEnv.getMessager();
        try {
            String className = "MyGeneratedEventBusSubscriberIndex";
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(className);
            try (BufferedWriter writer = new BufferedWriter(sourceFile.openWriter())) {
                writer.write("import de.greenrobot.event.SubscriberIndexEntry;\n");
                writer.write("import de.greenrobot.event.ThreadMode;\n\n");
                writer.write("/** This class is generated by EventBus, do not edit. */\n");
                writer.write("public class " + className + " {\n");
                writer.write("    public static final SubscriberIndexEntry[] INDEX = {\n");

                for (TypeElement annotation : annotations) {
                    Set<? extends Element> elements = env.getElementsAnnotatedWith(annotation);
                    for (Element element : elements) {
                        writeIndexEntry(writer, element, messager);
                    }
                }
                writer.write("    };\n}\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void writeIndexEntry(BufferedWriter writer, Element element, Messager messager) throws IOException {
        Subscribe subscribe = element.getAnnotation(Subscribe.class);
        if (element.getModifiers().contains(Modifier.STATIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Subscriber method must not be static", element);
            return;
        }

        if (!element.getModifiers().contains(Modifier.PUBLIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Subscriber method must be public", element);
            return;
        }

        Set<Modifier> subscriberClassModifiers = element.getEnclosingElement().getModifiers();
        if (!subscriberClassModifiers.contains(Modifier.PUBLIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Subscriber class must be public",
                    element.getEnclosingElement());
            return;
        }

        List<? extends VariableElement> parameters = ((ExecutableElement) element).getParameters();
        if (parameters.size() != 1) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Subscriber method must have exactly 1 parameter", element);
            return;
        }

        VariableElement param = parameters.get(0);
        DeclaredType paramType = (DeclaredType) param.asType();
        Set<Modifier> eventClassModifiers = paramType.asElement().getModifiers();
        if (!eventClassModifiers.contains(Modifier.PUBLIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Event type must be public: " + paramType, param);
            return;
        }

        String subscriberClass = element.getEnclosingElement().asType().toString();
        Name methodName = element.getSimpleName();
        writer.append("        new SubscriberIndexEntry(\n");
        writer.append("            ").append(subscriberClass).append(".class,\n");
        writer.append("            \"").append(methodName).append("\",\n");
        writer.append("            ").append(paramType.toString()).append(".class,\n");
        writer.append("            ThreadMode.").append(subscribe.threadMode().name()).append("),\n");

        messager.printMessage(Diagnostic.Kind.NOTE, "Indexed @Subscribe at " +
                element.getEnclosingElement().getSimpleName() + "." + methodName +
                "(" + paramType.asElement().getSimpleName() + ")");
    }
}
