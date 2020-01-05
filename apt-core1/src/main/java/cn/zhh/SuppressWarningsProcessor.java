package cn.zhh;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

/**
 * SuppressWarnings处理器
 *
 * @author Zhou Huanghua
 * @date 2020/1/5 14:19
 */
public class SuppressWarningsProcessor extends AbstractProcessor {

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(SuppressWarnings.class).forEach(element -> {
            logger.info(String.format("element %s has been processed.", element.getSimpleName()));
        });
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(SuppressWarnings.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
