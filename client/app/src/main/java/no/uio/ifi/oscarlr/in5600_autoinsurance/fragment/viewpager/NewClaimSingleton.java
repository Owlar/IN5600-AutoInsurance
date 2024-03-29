package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment.viewpager;

import java.util.ArrayList;
import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;

/* Singleton for add/replace claim in ViewPager2 */
public class NewClaimSingleton {
    private static NewClaimSingleton instance;
    private Claim newClaim;
    private List<Claim> claims = new ArrayList<>();

    private NewClaimSingleton () {

    }

    public static synchronized NewClaimSingleton getInstance() {
        if (instance == null) {
            instance = new NewClaimSingleton();
        }
        return instance;
    }

    public String getNumberOfClaims() {
        return String.valueOf(claims.size());
    }

    public Claim getClaim(int replaceClaimWithID) {
        if (replaceClaimWithID == -1) {
            return newClaim;
        }
        else {
            return claims.get(replaceClaimWithID);
        }
    }

    public void initNewClaim() {
        newClaim = new Claim();
        newClaim.setClaimId(String.valueOf(claims.size()));
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }
}
