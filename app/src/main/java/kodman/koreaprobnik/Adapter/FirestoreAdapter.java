package kodman.koreaprobnik.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kodman.koreaprobnik.Util.Cnst;

public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements EventListener<QuerySnapshot> {

    private static final String TAG = "FirestoreAdapter";
    // Переменные
    // Запрос
    private Query mQuery;

    //для добавления с параметрами
    private boolean flag = false;
    private String title;

    // Подписка
    private ListenerRegistration mRegistration;
    // Лист
    private ArrayList<DocumentSnapshot> mSnapshots = new ArrayList<>();

    // Конструктор
    public FirestoreAdapter(Query query) {
        mQuery = query;
    }

    public FirestoreAdapter(Query query, String title) {
        mQuery = query;
       this.title =title;
    }

    // переписанный метод интерфейса EventListener
    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "onEvent: ошибка", e);
            onError(e);
            return;
        }

        // Запускаем event в зависимости от изменения, произошедшего в Firestore
        Log.d(TAG, "---onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED:// добавление
                    if (title != null)
                        onDocumentAdded(change, title);
                    else
                        onDocumentAdded(change);
                    break;
                case MODIFIED:// изменение
                    onDocumentModified(change);
                    break;
                case REMOVED:// удаление
                    onDocumentRemoved(change);
                    break;
            }
        }

        onDataChanged();
    }

    // Начинаем слушать на запросе в Firestore
    public void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    // Прекращаем слушать
    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        mSnapshots.clear();
        notifyDataSetChanged();
    }

    // Устанавливаем запрос
    public void setQuery(Query query) {
        // Прекращаем слушать
        stopListening();

        // Удаляем существующие данные
        mSnapshots.clear();
        notifyDataSetChanged();

        // Устанавливаем новый запрос
        mQuery = query;
        startListening();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    protected DocumentSnapshot getSnapshot(int index) {
        return mSnapshots.get(index);
    }

    // данные добавлены
    protected void onDocumentAdded(DocumentChange change) {
        Log.d("---", "DocAdded : " + change);
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    protected void onDocumentAdded(DocumentChange change, String title) {
        Log.d("---", "DocAdded withparams: " + change);
        if(change.getDocument().getData().get(Cnst.TITLE).toString().contains(title)
                ||change.getDocument().getData().get(Cnst.DESCRIPTION).toString().contains(title))
        {
            mSnapshots.add( change.getDocument());
            Log.d("---", "DocAdded withparams Title: " + title);

            Log.d("---", "DocAdded withparams Title: " + title);

            notifyItemInserted(change.getNewIndex());
        }
    }

    // данные изменены
    protected void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    // данные удалены
    protected void onDocumentRemoved(DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    // ошибка
    protected void onError(FirebaseFirestoreException e) {
    }

    ;

    // метод имплементируется (определяется) наследуемымм классами
    protected void onDataChanged() {
    }
}
