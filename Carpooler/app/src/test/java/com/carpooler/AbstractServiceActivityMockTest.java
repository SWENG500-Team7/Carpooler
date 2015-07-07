package com.carpooler;

import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.UserDataService;
import com.carpooler.dao.dto.UserData;
import com.carpooler.users.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.model.people.Person;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * Created by raymond on 7/6/15.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractServiceActivityMockTest {
    @InjectMocks
    protected TestServiceActivityCallback callback;
    @Mock
    protected Person testPerson;
    @Mock
    protected UserDataService userDataService;
    @Mock
    protected People people;
    @Mock
    protected PendingResult<People.LoadPeopleResult> loadPeopleResultPendingResult;
    @Mock
    protected People.LoadPeopleResult loadPeopleResult;
    @Captor
    protected ArgumentCaptor<ResultCallback<People.LoadPeopleResult>> loadPeopleResultResultCallback;
    protected User user;


    @Before
    public void setup() throws RemoteException {
        Mockito.when(testPerson.getId()).thenReturn("testuser");
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                DatabaseService.GetCallback<UserData> callback = (DatabaseService.GetCallback<UserData>) invocation.getArguments()[1];
                callback.doSuccess(new UserData());
                return Void.TYPE;
            }
        }).when(userDataService).getUserData(Mockito.anyString(), Mockito.isA(DatabaseService.GetCallback.class));
        user = new User(testPerson, callback);
        callback.setUser(user);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return loadPeopleResultPendingResult;
            }
        }).when(people).load(Mockito.any(GoogleApiClient.class), (String[]) Mockito.anyVararg());
        Mockito.doNothing().when(loadPeopleResultPendingResult).setResultCallback(loadPeopleResultResultCallback.capture());
    }
}
