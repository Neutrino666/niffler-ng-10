package guru.qa.niffler.model;

import lombok.NonNull;

public record StaticUser(@NonNull String username,
                         @NonNull String password,
                         String friend,
                         String income,
                         String outcome) {

}
