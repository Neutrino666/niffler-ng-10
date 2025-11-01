package guru.qa.niffler.service.spend;

import guru.qa.niffler.model.SpendJson;

public interface SpendClient {

  SpendJson create(SpendJson spend);
}
