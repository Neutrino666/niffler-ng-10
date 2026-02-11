package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Error;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.FriendCategoryAccessErrorQuery;
import guru.qa.RecursionErrorForFriendsQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GRAPHQL Friends")
public class FriendsGqlTest extends BaseGraphQlTest {

  @User
  @Test
  @ApiLogin
  @DisplayName("[Negative] Рекурсивный запрос друзей - 1+ вложенностей")
  void recursionFriendsIsNotPresent(@Token String bearerToken) {
    final ApolloCall<RecursionErrorForFriendsQuery.Data> friendsCall = apolloClient.query(
            RecursionErrorForFriendsQuery.builder()
                .page(0)
                .size(10)
                .searchQuery(null)
                .build()
        )
        .addHttpHeader("authorization", bearerToken);

    final ApolloResponse<RecursionErrorForFriendsQuery.Data> response = Rx2Apollo
        .single(friendsCall).blockingGet();
    Assertions.assertThat(response.errors)
        .isNotEmpty()
        .extracting(Error::getMessage)
        .containsExactly("Can`t fetch over 1 friends sub-queries");
  }

  @User(friends = 1)
  @Test
  @ApiLogin
  @DisplayName("[Negative] Просмотр категорий друзей")
  void friendsCategoriesIsNotPresent(@Token String bearerToken) {
    final ApolloCall<FriendCategoryAccessErrorQuery.Data> friendsCall = apolloClient.query(
            FriendCategoryAccessErrorQuery.builder()
                .page(0)
                .size(10)
                .searchQuery(null)
                .build()
        )
        .addHttpHeader("authorization", bearerToken);

    final ApolloResponse<FriendCategoryAccessErrorQuery.Data> response = Rx2Apollo
        .single(friendsCall).blockingGet();
    Assertions.assertThat(response.errors)
        .isNotEmpty()
        .extracting(Error::getMessage)
        .containsExactly("Can`t query categories for another user");
  }
}
