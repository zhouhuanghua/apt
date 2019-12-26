package cn.zhh;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * ToJson APT
 *
 * @author Zhou Huanghua
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("lombok.Data")
public class DataProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elementSet = roundEnv.getElementsAnnotatedWith(annotation);
            System.out.println(String.format("带注解%s的元素：%s", annotation.getSimpleName(),
                    elementSet.stream().map(Element::getSimpleName).map(Name::toString)
                            .reduce((n1, n2) -> n1 + "、" + n2).orElse("")));
        }
        return false;
    }
}
