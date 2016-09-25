package com.example.suzukitakahiro.trainalert.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns;
import com.example.suzukitakahiro.trainalert.Db.MasterDb.PrefDao;
import com.example.suzukitakahiro.trainalert.R;

/**
 * 都道府県選択フラグメント
 *
 * @author suzukitakahiro on 16/09/21.
 */
public class PrefFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private Context mContext;
    private View mView;
    private SimpleCursorAdapter mSimpleCursorAdapter;
    
    private static final int FIND_ALL = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        mView = inflater.inflate(R.layout.fragment_select, container, false);
        
        setListView();
        
        // 都道府県リスト読み込みスタート
        getActivity().getSupportLoaderManager().initLoader(FIND_ALL, null, this);
        return mView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> cursorLoader = null;
        PrefDao prefDao = new PrefDao(mContext);
        switch (id) {

            // 全件検索
            case FIND_ALL:
                cursorLoader = prefDao.findAll();
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSimpleCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    /** 
     * 一覧を作成
     */
    private void setListView() {
        String[] from = {MasterColumns.PREF_NAME};
        int[] to = {R.id.select_list_item_title};

        mSimpleCursorAdapter = new SimpleCursorAdapter
                (mContext, R.layout.list_item_select, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        ListView listView = (ListView)mView.findViewById(R.id.select_list_view);
        listView.setAdapter(mSimpleCursorAdapter);

        listView.setOnItemClickListener(this);
    }
    
    /**
     * リスト選択時の処理
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long prefCd) {

        // 選択されたid（prefCdに今回は等しい）の情報を付与して路線画面へ遷移
        Bundle bundle = new Bundle();
        bundle.putLong(MasterColumns.PREF_CD, prefCd);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment lineFragment = new LineFragment();
        lineFragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, lineFragment);
        transaction.commit();
    }
}
