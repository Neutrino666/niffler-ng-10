package guru.qa.niffler.page.components;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.match;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ParametersAreNonnullByDefault
public class Calendar extends BaseComponent<Calendar> {

  private final SelenideElement dateFilled = $("input[ name = 'date' ]");
  private final SelenideElement calendarBtn = $("button[ aria-label *= 'Choose date' ]");

  private final SelenideElement currentDate = self.$(".MuiPickersCalendarHeader-label");
  private final SelenideElement nextMonth = self.$(".MuiIconButton-edgeStart");
  private final SelenideElement prevMonth = self.$(".MuiIconButton-edgeEnd");
  private final SelenideElement switchViewBtn = self.$(
      ".MuiPickersCalendarHeader-switchViewButton");

  private final ElementsCollection years = self.$$(".MuiPickersYear-yearButton");
  private final ElementsCollection days = self.$$(".MuiPickersDay-root");

  public Calendar() {
    super($(".MuiDateCalendar-root"));
  }

  public @Nonnull Calendar selectDateInCalendar(Date date) {
    open(true);

    LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
    switchView(View.YEAR);
    selectYear(localDate.getYear());
    selectMonth(localDate.getMonthValue());
    selectDay(localDate.getDayOfMonth());
    checkDate(localDate);
    return this;
  }

  private @Nonnull Calendar selectYear(Integer year) {
    years.find(text(String.valueOf(year)))
        .scrollIntoCenter()
        .click();
    return this;
  }

  private @Nonnull Calendar checkDate(LocalDate date) {
    String actualDate = dateFilled.val();
    assertThat(actualDate)
        .isNotEmpty();
    assertThat(LocalDate.parse(actualDate, DateTimeFormatter.ofPattern("MM/dd/yyyy")))
        .isEqualTo(date);
    return this;
  }

  private @Nonnull Calendar selectMonth(Integer month) {
    Integer currentMonth = Month.valueOf(getCurrentDate()[0].toUpperCase())
        .getValue();
    if (month.equals(currentMonth)) {
      return this;
    }

    while (!month.equals(currentMonth)) {
      if (currentMonth > month) {
        prevMonth.click();
        currentMonth--;
      } else {
        nextMonth.click();
        currentMonth++;
      }
    }

    return this;
  }

  private @Nonnull Calendar selectDay(Integer day) {
    days.find(text(String.valueOf(day)))
        .click();
    return this;
  }

  private @Nonnull Calendar open(boolean state) {
    if (self.exists() != state) {
      calendarBtn.click();
    }
    self.shouldBe(state ? visible : not(exist));
    return this;
  }

  private @Nonnull Calendar switchView(View view) {
    String stateView = switchViewBtn.getAttribute("aria-label");
    if (stateView != null && !stateView.startsWith(view.getValue())) {
      switchViewBtn.click();
    }
    switchViewBtn.should(match(
            "Проверяем что вид соответствует ожидаемому: " + view,
            el -> {
              String text = el.getAttribute("aria-label");
              return text != null && text.startsWith(view.getValue());
            }
        )
    );
    return this;
  }

  private @Nonnull String[] getCurrentDate() {
    return currentDate.should(matchText("^[JFAMSOND][a-y]{2,8} \\d{4}$"))
        .getText()
        .split(" ");
  }

  @RequiredArgsConstructor
  @Getter
  @ToString
  private enum View {
    YEAR("year view "),
    MONTH("calendar view ");

    @Nonnull
    private final String value;
  }
}
