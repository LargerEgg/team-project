package use_case;

import use_case.signup.*;
import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignupInteractorTest {

    private FakeUserDAO userDAO;
    private FakeSignupPresenter presenter;
    private FakeUserFactory userFactory;
    private SignupInteractor interactor;

    @BeforeEach
    void setup() {
        userDAO = new FakeUserDAO();
        presenter = new FakeSignupPresenter();
        userFactory = new FakeUserFactory();
        interactor = new SignupInteractor(userDAO, presenter, userFactory);
    }

    // ---------- TEST 1: USER ALREADY EXISTS ----------
    @Test
    void testUserAlreadyExists() {
        userDAO.exists = true;

        SignupInputData input = new SignupInputData("john", "123", "123");
        interactor.execute(input);

        assertEquals("User already exists", presenter.failMessage);
        assertNull(presenter.successData);
    }

    // ---------- TEST 2: PASSWORDS DO NOT MATCH ----------
    @Test
    void testPasswordsDoNotMatch() {
        SignupInputData input = new SignupInputData("john", "123", "456");
        interactor.execute(input);

        assertEquals("Passwords don't match", presenter.failMessage);
    }

    // ---------- TEST 3: EMPTY PASSWORD ----------
    @Test
    void testEmptyPassword() {
        SignupInputData input = new SignupInputData("john", "", "");
        interactor.execute(input);

        assertEquals("New password cannot be empty", presenter.failMessage);
    }

    // ---------- TEST 4: EMPTY USERNAME ----------
    @Test
    void testEmptyUsername() {
        SignupInputData input = new SignupInputData("", "123", "123");
        interactor.execute(input);

        assertEquals("Username cannot be empty", presenter.failMessage);
    }

    // ---------- TEST 5: SUCCESS SIGNUP ----------
    @Test
    void testSignupSuccess() {
        SignupInputData input = new SignupInputData("john", "123", "123");

        interactor.execute(input);

        assertNotNull(presenter.successData);
        assertEquals("john", presenter.successData.getUsername());
        assertTrue(userDAO.saveWasCalled);
    }

    // ---------- TEST 6: switchToLoginView ----------
    @Test
    void testSwitchToLoginView() {
        interactor.switchToLoginView();
        assertTrue(presenter.switchCalled);
    }


    // --------------- FAKE CLASSES FOR TESTING -----------------

    private static class FakeUserDAO implements SignupUserDataAccessInterface {
        boolean exists = false;
        boolean saveWasCalled = false;
        User savedUser = null;

        @Override
        public boolean existsByName(String username) {
            return exists;
        }

        @Override
        public void save(User user) {
            saveWasCalled = true;
            savedUser = user;
        }
    }

    private static class FakeSignupPresenter implements SignupOutputBoundary {
        String failMessage = null;
        SignupOutputData successData = null;
        boolean switchCalled = false;

        @Override
        public void prepareSuccessView(SignupOutputData data) {
            this.successData = data;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failMessage = errorMessage;
        }

        @Override
        public void switchToLoginView() {
            switchCalled = true;
        }
    }

    private static class FakeUserFactory extends UserFactory {
        @Override
        public User create(String username, String password) {
            return new User(username, password);
        }
    }
}
