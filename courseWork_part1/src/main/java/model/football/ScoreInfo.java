package model.football;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ScoreInfo(
        @JsonProperty("home")
        int home,
        @JsonProperty("away")
        int away,
        @JsonProperty("homeTeam")
        String homeTeam,
        @JsonProperty("awayTeam")
        String awayTeam
) {}
