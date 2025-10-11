package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.IssueExtension;
import io.qameta.allure.LinkAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtendWith(IssueExtension.class)
@LinkAnnotation(type = "ghIssue")
@Repeatable(DisabledByIssues.class)
public @interface DisabledByIssue {

  String value();
}
