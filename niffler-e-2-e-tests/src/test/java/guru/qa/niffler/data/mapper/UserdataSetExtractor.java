package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class UserdataSetExtractor implements ResultSetExtractor<UserEntity> {

  public static final UserdataSetExtractor INSTANCE = new UserdataSetExtractor();

  @Override
  public UserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
    Map<UUID, UserEntity> userMap = new ConcurrentHashMap<>();
    UUID userId = null;

    while (rs.next()) {
      userId = rs.getObject("id", UUID.class);
      UserEntity user = userMap.computeIfAbsent(userId, id -> {
        UserEntity result = new UserEntity();
        try {
          result.setId(rs.getObject("id", UUID.class));
          result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          result.setFirstname(rs.getString("firstname"));
          result.setFullname(rs.getString("full_name"));
          result.setPhoto(rs.getBytes("photo"));
          result.setPhotoSmall(rs.getBytes("photo_small"));
          result.setSurname(rs.getString("surname"));
          result.setUsername(rs.getString("username"));
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
        return result;
      });
      FriendshipEntity friendship = new FriendshipEntity();
      UUID requesterId = rs.getObject("requester_id", UUID.class);
      UUID addresseeId = rs.getObject("addressee_id", UUID.class);
      UserEntity requester = new UserEntity();
      UserEntity addressee = new UserEntity();
      String status = rs.getString("status");

      if (requesterId != null) {
        requester.setId(requesterId);
        friendship.setRequester(requester);
      }
      if (addresseeId != null) {
        addressee.setId(addresseeId);
        friendship.setAddressee(addressee);
      }
      if (status != null) {
        friendship.setStatus(FriendshipStatus.valueOf(status));
      }

      if (requesterId != null && requesterId.equals(user.getId())) {
        user.getFriendshipAddressees().add(friendship);
      } else if (addresseeId != null && addresseeId.equals(user.getId())) {
        user.getFriendshipRequests().add(friendship);
      }
    }
    return userMap.get(userId);
  }
}
