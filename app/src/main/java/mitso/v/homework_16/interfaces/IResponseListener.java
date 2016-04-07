package mitso.v.homework_16.interfaces;

public interface IResponseListener<T> {

    void onFinish(final boolean isSuccess, final T response);
}
