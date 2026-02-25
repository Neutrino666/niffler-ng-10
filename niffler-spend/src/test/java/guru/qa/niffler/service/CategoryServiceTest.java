package guru.qa.niffler.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  private CategoryEntity mainUserArchivedCategory;
  private CategoryEntity mainUserActiveCategory;
  private final String mainUsername = "MainUser";
  private final UUID mainUserArchivedCategoryUUID = UUID.randomUUID();
  private final UUID mainUserActiveCategoryUUID = UUID.randomUUID();

  @BeforeEach
  void init() {
    mainUserArchivedCategory = new CategoryEntity();
    mainUserArchivedCategory.setId(mainUserArchivedCategoryUUID);
    mainUserArchivedCategory.setName(mainUsername + " Archived");
    mainUserArchivedCategory.setUsername(mainUsername);
    mainUserArchivedCategory.setArchived(true);

    mainUserActiveCategory = new CategoryEntity();
    mainUserActiveCategory.setId(mainUserActiveCategoryUUID);
    mainUserActiveCategory.setName(mainUsername + " Archived");
    mainUserActiveCategory.setUsername(mainUsername);
    mainUserActiveCategory.setArchived(false);
  }

  @ValueSource(booleans = {true, false})
  @ParameterizedTest
  void getAllCategoriesShouldUseFilterExcludeArchived(
      boolean excludeArchived,
      @Mock CategoryRepository categoryRepository
  ) {
    final List<CategoryEntity> given = List.of(
        mainUserArchivedCategory,
        mainUserActiveCategory
    );

    when(categoryRepository.findAllByUsernameOrderByName(eq(mainUsername)))
        .thenReturn(given);

    final CategoryService categoryService = new CategoryService(categoryRepository);
    final List<CategoryJson> result = categoryService.getAllCategories(mainUsername, excludeArchived);

    final List<CategoryJson> expected = given.stream()
        .filter(ce -> !excludeArchived || !ce.isArchived())
        .map(CategoryJson::fromEntity)
        .toList();

    assertThat(result)
        .hasSize(expected.size())
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  void categoryNotFoundExceptionShouldBeThrown(
      @Mock CategoryRepository categoryRepository
  ) {
    when(categoryRepository.findByUsernameAndId(eq(mainUsername), eq(mainUserActiveCategoryUUID)))
        .thenReturn(Optional.empty());

    final CategoryService categoryService = new CategoryService(categoryRepository);

    assertThatThrownBy(() -> categoryService.update(CategoryJson.fromEntity(mainUserActiveCategory)))
        .isInstanceOf(CategoryNotFoundException.class)
        .hasMessage("Can`t find category by id: '%s'".formatted(mainUserActiveCategoryUUID));
    then(categoryRepository).should(times(1))
        .findByUsernameAndId(mainUsername, mainUserActiveCategoryUUID);
  }

  @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
  @ParameterizedTest
  void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();

    when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        catName,
        username,
        true
    );

    assertThatThrownBy(() -> categoryService.update(categoryJson))
        .isInstanceOf(InvalidCategoryNameException.class)
        .hasMessage("Can`t add category with name: '" + catName + "'");
    then(categoryRepository).should(times(0))
        .save(any());
  }

  @Test
  void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();
    cat.setId(id);
    cat.setUsername(username);
    cat.setName("Магазины");
    cat.setArchived(false);

    when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));
    when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "Бары",
        username,
        true
    );
    CategoryEntity expected = new CategoryEntity();
    expected.setId(categoryJson.id());
    cat.setUsername(categoryJson.username());
    cat.setName(categoryJson.name());
    cat.setArchived(categoryJson.archived());

    categoryService.update(categoryJson);

    ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue())
        .isEqualTo(expected);
  }

  @Test
  void categoryTooManyExceptionShouldBeThrown(
      @Mock CategoryRepository categoryRepository
  ) {
    when(categoryRepository.findByUsernameAndId(eq(mainUsername), eq(mainUserActiveCategoryUUID)))
        .thenReturn(Optional.of(mainUserArchivedCategory));
    when(categoryRepository.countByUsernameAndArchived(eq(mainUsername), eq(false)))
        .thenReturn(8L);

    final CategoryService categoryService = new CategoryService(categoryRepository);

    assertThatThrownBy(() -> categoryService.update(CategoryJson.fromEntity(mainUserActiveCategory)))
        .isInstanceOf(TooManyCategoriesException.class)
        .hasMessage("Can`t unarchive category for user: '%s'".formatted(mainUsername));
    then(categoryRepository).should(times(1))
        .countByUsernameAndArchived(mainUsername, false);
  }

  @Test
  void addCategoryShouldSave(
      @Mock CategoryRepository categoryRepository
  ) {
    when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);
    final CategoryService categoryService = new CategoryService(categoryRepository);
    final CategoryJson category = CategoryJson.fromEntity(mainUserActiveCategory);

    final CategoryJson categoryJson = categoryService.addCategory(category);

    then(categoryRepository).should(times(1)).save(any());
    assertThat(categoryJson)
        .isEqualTo(new CategoryJson(
            null,
            category.name(),
            category.username(),
            category.archived()
        ));
  }

  @Test
  void getOrSaveShouldSaveIfNotFound(
      @Mock CategoryRepository categoryRepository
  ) {
    final CategoryJson category = CategoryJson.fromEntity(mainUserActiveCategory);

    when(categoryRepository.findByUsernameAndName(eq(mainUsername), eq(mainUserActiveCategory.getName())))
        .thenReturn(Optional.empty());
    when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);
    final CategoryService categoryService = new CategoryService(categoryRepository);
    final CategoryEntity result = categoryService.getOrSave(category);

    assertThat(result)
        .matches(r -> category.username().equals(r.getUsername()), "username")
        .matches(r -> category.name().equals(r.getName()), "name")
        .matches(r -> category.archived() == r.isArchived(), "archived");
    then(categoryRepository).should(times(1)).save(result);
  }

  @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
  @ParameterizedTest
  void saveInvalidCategoryNameExceptionShouldBeThrow(
      String catName, @Mock CategoryRepository categoryRepository
  ) {
    final CategoryJson category = new CategoryJson(
        null,
        catName,
        null,
        true
    );

    final CategoryService categoryService = new CategoryService(categoryRepository);

    assertThatThrownBy(() -> categoryService.save(category))
        .isInstanceOf(InvalidCategoryNameException.class)
        .hasMessage("Can`t add category with name: '%s'".formatted(category.name()));
    then(categoryRepository).should(times(0))
        .save(any());
  }

  @Test
  void saveTooManyCategoriesExceptionShouldBeThrow(
      @Mock CategoryRepository categoryRepository
  ) {
    when(categoryRepository.countByUsernameAndArchived(eq(mainUsername), eq(false)))
        .thenReturn(8L);
    final CategoryJson category = CategoryJson.fromEntity(mainUserActiveCategory);

    final CategoryService categoryService = new CategoryService(categoryRepository);

    assertThatThrownBy(() -> categoryService.save(category))
        .isInstanceOf(TooManyCategoriesException.class)
        .hasMessage("Can`t add over than 8 categories for user: '%s'".formatted(category.username()));
    then(categoryRepository).should(times(1))
        .countByUsernameAndArchived(mainUsername, false);
    then(categoryRepository).should(times(0))
        .save(any());
  }

  @Test
  void saveShouldCallSaveMethod(
      @Mock CategoryRepository categoryRepository
  ) {
    when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(answer -> answer.getArguments()[0]);
    final CategoryService categoryService = new CategoryService(categoryRepository);
    final CategoryJson category = CategoryJson.fromEntity(mainUserActiveCategory);

    final CategoryEntity categoryJson = categoryService.save(category);

    then(categoryRepository).should(times(1)).save(any());
    assertThat(categoryJson)
        .matches(r -> category.username().equals(r.getUsername()), "username")
        .matches(r -> category.name().equals(r.getName()), "name")
        .matches(r -> category.archived() == r.isArchived(), "archived");
  }
}