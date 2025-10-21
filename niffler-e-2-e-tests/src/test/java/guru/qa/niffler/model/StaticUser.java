package guru.qa.niffler.model;

import javax.annotation.Nonnull;

public record StaticUser(@Nonnull String username,
                         @Nonnull String password,
                         String friend,
                         String income,
                         String outcome) {

}
