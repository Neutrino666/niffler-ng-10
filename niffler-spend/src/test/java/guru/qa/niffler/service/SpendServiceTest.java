package guru.qa.niffler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.ex.SpendNotFoundException;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpendServiceTest {

  @Test
  void getSpendsForUserShouldThrowExceptionInCaseThatIdIsIncorrectFormat(
      @Mock SpendRepository spendRepository,
      @Mock CategoryService categoryService) {

    final String incorrectId = "incorrect";
    SpendService spendService = new SpendService(spendRepository, categoryService);

    assertThatThrownBy(() -> spendService.getSpendForUser(incorrectId, "name"))
        .isInstanceOf(SpendNotFoundException.class)
        .hasMessage("Can`t find spend by given id: " + incorrectId);
  }

  @Test
  void getSpendsForUserShouldThrowExceptionInCaseThatSpendNotFoundIdDb(
      @Mock SpendRepository spendRepository,
      @Mock CategoryService categoryService) {

    final UUID correctId = UUID.randomUUID();
    final String correctUsername = "name";

    SpendService spendService = new SpendService(spendRepository, categoryService);
    Mockito.when(spendRepository.findByIdAndUsername(eq(correctId), eq(correctUsername)))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> spendService.getSpendForUser(correctId.toString(), "name"))
        .isInstanceOf(SpendNotFoundException.class)
        .hasMessage("Can`t find spend by given id: " + correctId);
  }

  @Test
  void getSpendsForUserShouldReturnCorrectJsonObject(
      @Mock SpendRepository spendRepository,
      @Mock CategoryService categoryService) {

    final UUID correctId = UUID.randomUUID();
    final String correctUsername = "name";
    final SpendEntity spend = new SpendEntity();
    CategoryEntity category = new CategoryEntity();
    spend.setId(correctId);
    spend.setUsername(correctUsername);
    spend.setCurrency(CurrencyValues.USD);
    spend.setAmount(150.15);
    spend.setDescription("unit-test-description");
    spend.setSpendDate(new Date(0));
    category.setUsername(correctUsername);
    category.setName("unit-test-category");
    category.setArchived(false);
    category.setId(UUID.randomUUID());
    spend.setCategory(category);

    SpendService spendService = new SpendService(spendRepository, categoryService);
    Mockito.when(spendRepository.findByIdAndUsername(eq(correctId), eq(correctUsername)))
        .thenReturn(Optional.of(
            spend
        ));
    SpendJson actual = spendService.getSpendForUser(correctId.toString(), "name");
    Mockito.verify(spendRepository, times(1))
        .findByIdAndUsername(eq(correctId), eq(correctUsername));

    assertThat(actual)
        .extracting(SpendJson::description)
        .isEqualTo(spend.getDescription());
  }
}