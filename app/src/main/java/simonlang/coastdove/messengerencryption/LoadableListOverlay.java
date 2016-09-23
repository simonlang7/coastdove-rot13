package simonlang.coastdove.messengerencryption;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 *
 */
public class LoadableListOverlay<T> implements LoaderManager.LoaderCallbacks<ArrayList<T>> {

    protected ArrayAdapter<T> mAdapter;
    protected int mLoaderID;

    @Override
    public Loader<ArrayList<T>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<T>> loader, ArrayList<T> data) {

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<T>> loader) {

    }
}
