package de.bmarwell.ffb.depot.client;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertTrue;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.bmarwell.ffb.depot.client.err.FfbClientError;
import de.bmarwell.ffb.depot.client.value.FfbDepotNummer;
import de.bmarwell.ffb.depot.client.value.FfbLoginKennung;
import de.bmarwell.ffb.depot.client.value.FfbPin;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.core.NewCookie;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCookies {

  private static final Logger LOG = LoggerFactory.getLogger(TestCookies.class);


  private static final FfbLoginKennung LOGIN = FfbLoginKennung.of("22222301");
  private static final FfbPin PIN = FfbPin.of("91901");

  private static final FfbDepotNummer DEPOT = FfbDepotNummer.of("222223");

  @Rule
  public WireMockRule wiremock = new WireMockRule(wireMockConfig().dynamicPort());

  private FfbMobileClient client;

  @Before
  public void setUp() throws Exception {
    final FfbClientConfiguration config = () -> URI.create("http://localhost:" + wiremock.port());

    this.client = new FfbMobileClient(LOGIN, PIN, config);
  }

  @Test
  public void testFfbMobileClientFfbLoginKennungFfbPin() throws MalformedURLException, FfbClientError {
    boolean sessionIdFound = false;
    client.logon();
    final Map<String, NewCookie> cookies = client.getCookies();

    for (Entry<String, NewCookie> cookie : cookies.entrySet()) {
      /* At least one of the cookies should be a JSESSIONID. */
      if ("JSESSIONID".equals(cookie.getKey())) {
        sessionIdFound = true;
      }

      LOG.debug("Cookie: [{}].", cookie);
    }

    client.logout();
    assertTrue("SessionId has not been found -- user never logged in?", sessionIdFound);
  }

}