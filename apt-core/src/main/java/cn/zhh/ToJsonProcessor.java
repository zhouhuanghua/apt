package cn.zhh;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ToJson APT
 *
 * @author Zhou Huanghua
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("cn.zhh.ToJson")
public class ToJsonProcessor extends AbstractProcessor {

    /**
     * 抽象语法树
     */
    private JavacTrees trees;

    /**
     * AST
     */
    private TreeMaker treeMaker;

    /**
     * 标识符
     */
    private Names names;

    /**
     * 日志处理
     */
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment)processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        messager = processingEnvironment.getMessager();
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotation = roundEnv.getElementsAnnotatedWith(ToJson.class);
        annotation.stream().map(element -> trees.getTree(element)).forEach(tree -> tree.accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClass) {
                // 过滤属性
                Map<Name, JCTree.JCVariableDecl> treeMap =
                        jcClass.defs.stream()
                                .filter(k -> Objects.equals(k.getKind(), Tree.Kind.VARIABLE))
                                .map(tree -> (JCTree.JCVariableDecl) tree)
                                .collect(Collectors.toMap(JCTree.JCVariableDecl::getName, Function.identity()));
                try {
                    // 增加toJson方法
                    jcClass.defs = jcClass.defs.prepend(generateToJsonMethod(treeMap));
                } catch (Exception e) {
                    messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                }
                super.visitClassDef(jcClass);
            }

            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethod) {
            }
        }));
        return true;
    }

    private JCTree generateToJsonMethod(Map<Name, JCTree.JCVariableDecl> treeMap) {
        // 修改方法级别
        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);

        // 添加方法名称
        Name methodName = names.fromString("toJson");

        // 设置方法体
        JCTree.JCExpressionStatement statement = treeMaker.Exec(treeMaker.Apply(List.of(memberAccess("java.lang.Object")),
                memberAccess("com.nicky.lombok.adapter.AdapterFactory.builderStyleAdapter"),
                List.of(treeMaker.Ident(names.fromString("this")))));
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();
        jcStatements.append(treeMaker.Return(statement.getExpression()));
        JCTree.JCBlock jcBlock = treeMaker.Block(0, jcStatements.toList());

        // 添加返回值类型
        JCTree.JCExpression returnType = memberAccess("java.lang.String");

        // 参数类型
        List<JCTree.JCTypeParameter> typeParameters = List.nil();

        // 参数变量
        List<JCTree.JCVariableDecl> parameters = List.nil();

        // 声明异常
        List<JCTree.JCExpression> throwsClauses = List.nil();

        return treeMaker.MethodDef(modifiers, methodName, returnType, typeParameters, parameters, throwsClauses, jcBlock, null);
    }

    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(names.fromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, names.fromString(componentArray[i]));
        }
        return expr;
    }


}
