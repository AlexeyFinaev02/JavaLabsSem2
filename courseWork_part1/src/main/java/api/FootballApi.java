package api;

public class FootballApi implements ApiClient {

    @Override
    public String getData() {
        String query = "https://www.thesportsdb.com/api/v1/json/3/lookupevent.php?id=2267452";
        return messenger.getBody(query);
    }

    @Override
    public String getName() {
        return "football";
    }
}