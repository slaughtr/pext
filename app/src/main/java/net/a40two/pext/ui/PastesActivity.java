package net.a40two.pext.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import net.a40two.pext.R;
import net.a40two.pext.adapters.PasteListAdapter;
import net.a40two.pext.models.Paste;
import net.a40two.pext.services.PastebinListService;
import net.a40two.pext.ui.fragments.TrendingPastesFragment;
import net.a40two.pext.ui.fragments.UserPastesFragment;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PastesActivity extends AppCompatActivity {
    String fragToLoad = "";
    private TrendingPastesFragment trendingFrag;
    public ArrayList<Paste> mPastes = new ArrayList<>();
    private PasteListAdapter mAdapter;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pastes);

        fragToLoad = getIntent().getStringExtra("fragToLoad");

        if (fragToLoad.equals("")) {
            Log.d("onCreatePastesActivity", "in empty if");
            //go back home, there was a problem
        } else if (fragToLoad.equals("trending")) {
            getTrendingPastes();
            Log.d("onCreatePastesActivity", "in trending if");
            setTitle("Today's Trending Pastes");
        } else if (fragToLoad.equals("ownPastes")) {
            Log.d("onCreatePastesActivity", "in own if");
            setTitle("Your Pastes");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void getTrendingPastes() {
        final PastebinListService pblService = new PastebinListService();

        pblService.buildListUrl("trending", new Callback() {

            @Override public void onFailure(Call call, IOException e) { e.printStackTrace(); }

            @Override public void onResponse(Call call, Response response) throws IOException {

                mPastes = pblService.processResults("trending", response);
                    FragmentManager fm = getSupportFragmentManager();
//                    Log.d("onCreatePastesActivity", "in trending if");
                    fm.beginTransaction().replace(R.id.fragmentHolder, TrendingPastesFragment.newInstance(mPastes)).commit();
            }
        });
    }
}
