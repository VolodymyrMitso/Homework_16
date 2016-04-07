package mitso.v.homework_16;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import mitso.v.homework_16.constants.Constants;
import mitso.v.homework_16.enums.TypeRequest;
import mitso.v.homework_16.interfaces.IEventHandler;
import mitso.v.homework_16.interfaces.IResponseListener;
import mitso.v.homework_16.models.Cat;
import mitso.v.homework_16.recycler_view.CatAdapter;
import mitso.v.homework_16.recycler_view.SpacingDecoration;
import mitso.v.homework_16.utils.AsyncRequest;

public class MainActivity extends Activity implements IEventHandler {

    private RecyclerView        mRecyclerView_Cats;
    private CatAdapter          mCatAdapter;
    private ArrayList<Cat>      mCatList;

    private AsyncRequest<?>     mAsyncRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCatList = new ArrayList<>();
        mCatList.add(new Cat());

        mRecyclerView_Cats = (RecyclerView) findViewById(R.id.rv_Cats_AM);
        mCatAdapter = new CatAdapter(mCatList);
        mRecyclerView_Cats.setAdapter(mCatAdapter);
        mRecyclerView_Cats.setLayoutManager(new GridLayoutManager(this, 3));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.d_size_10dp);
        mRecyclerView_Cats.addItemDecoration(new SpacingDecoration(spacingInPixels, spacingInPixels, true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCatAdapter.setIEventHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCatAdapter.releaseIEventHandler();
    }

    @Override
    public void catOnClick(final int position) {
        if (checkStatusNetworks()) {
            if (position == mCatList.size() - 1)
                addCatDialog();
            else
                showDeleteCatDialog(position);
        } else
            Toast.makeText(MainActivity.this, getResources().getString(R.string.s_wiFi), Toast.LENGTH_SHORT).show();
    }

    private boolean checkStatusNetworks(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo    = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return ((wifiInfo != null && wifiInfo.isConnected()) || (networkInfo != null && networkInfo.isConnected()));
    }

    private void addCatDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        LinearLayout linearLayout_Params = (LinearLayout) inflater.inflate(R.layout.params_dialog, null);

        Spinner spinner_ChooseCategory = (Spinner) linearLayout_Params.findViewById(R.id.sp_ChooseCategory_PD);
        ArrayAdapter<?> adapterCategory = ArrayAdapter.createFromResource(this, R.array.sa_categories, android.R.layout.simple_spinner_dropdown_item);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_ChooseCategory.setAdapter(adapterCategory);
        final String[] category = new String[1];
        spinner_ChooseCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                String[] choose = getResources().getStringArray(R.array.sa_categories);
                category[0] = choose[selectedItemPosition];
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinner_ChooseNumber = (Spinner) linearLayout_Params.findViewById(R.id.sp_ChooseNumber_PD);
        ArrayAdapter<?> adapterNumber = ArrayAdapter.createFromResource(this, R.array.sa_numbers, android.R.layout.simple_spinner_dropdown_item);
        adapterNumber.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_ChooseNumber.setAdapter(adapterNumber);
        final String[] number = new String[1];
        spinner_ChooseNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                String[] choose = getResources().getStringArray(R.array.sa_numbers);
                number[0] = choose[selectedItemPosition];
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setView(linearLayout_Params)
                .setPositiveButton(getResources().getString(R.string.s_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addCatRequest(category[0], number[0]);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.s_back), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false);
        alertDialog.show();
    }

    private void addCatRequest(String category, String number) {
        if (mAsyncRequest == null || mAsyncRequest.getStatus().equals(AsyncTask.Status.FINISHED)) {
            (mAsyncRequest = new AsyncRequest<Cat[]>()
                    .typeRequest(TypeRequest.GET)
                    .classType(Cat[].class)
                    .responseListener(new IResponseListener<Cat[]>() {
                        @Override
                        public void onFinish(boolean isSuccess, Cat[] response) {
                            if (isSuccess) {
                                mCatList.remove(mCatList.size() - 1);
                                Collections.addAll(mCatList, response);
                                mCatList.add(new Cat());
                                mCatAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .addParam("results_per_page", number)
                    .addParam("category", category)
                    .addParam("format", "xml")
                    .addParam("size", "full"))
                    .execute(Constants.BASE_URL + Constants.GET_IMAGES);
        }
    }

    private void showDeleteCatDialog(final int position) {
        (new AsyncRequest<Bitmap>()
                .typeRequest(TypeRequest.GET)
                .classType(Bitmap.class)
                .responseListener(new IResponseListener<Bitmap>() {
                    @Override
                    public void onFinish(boolean isSuccess, Bitmap response) {

                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        LinearLayout linearLayout_CatDialog = (LinearLayout) inflater.inflate(R.layout.cat_dialog, null);
                        ImageView imageView = (ImageView) linearLayout_CatDialog.findViewById(R.id.iv_CatImage_CD);
                        imageView.setImageBitmap(response);

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setView(linearLayout_CatDialog)
                                .setPositiveButton(getResources().getString(R.string.s_delete), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mCatList.remove(position);
                                        mCatAdapter.notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.s_back), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setCancelable(false);
                        alertDialog.show();

                    }
                })).execute(mCatList.get(position).getUrl());
    }
}