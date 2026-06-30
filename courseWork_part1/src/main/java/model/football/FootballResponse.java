package model.football;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record FootballResponse(
        @JsonProperty("matches")
        List<FootballMatch> matches
) {}