package use_case;

import use_case.login.*;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginInteractorTest {

    private FakeUserDAO userDAO;
    private FakeLoginPresenter presenter;
    private LoginInteractor interactor;

    @BeforeEach
    void setup() {
        userDAO = new FakeUserDAO();
        presenter = new FakeLoginPresenter();
        interactor = new LoginInteractor(userDAO, presenter);
    }

    @Test
    void testAccountDoesNotExist() {
        LoginInputData input = new LoginInputData("john", "123");
        interactor.execute(input);

        assertEquals("john: Account does not exist.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void testUserIsNull() {
        userDAO.exists = true;     // pretend user exists
        userDAO.userToReturn = null;  // but actual get() returns null

        LoginInputData input = new LoginInputData("john", "123");
        interactor.execute(input);

        assertEquals("An unexpected error occurred: User data not found.", presenter.failMessage);
    }

    @Test
    void testWrongPassword() {
        userDAO.exists = true;
        userDAO.userToReturn = new User("john", "correctPassword");

        LoginInputData input = new LoginInputData("john", "wrongPassword");
        interactor.execute(input);

        assertEquals("Incorrect password for john.", presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void testSuccessfulLogin() {
        userDAO.exists = true;
        userDAO.userToReturn = new User("john", "123");

        LoginInputData input = new LoginInputData("john", "123");
        interactor.execute(input);

        assertNotNull(presenter.successData);
        assertEquals("john", presenter.successData.getUsername());
        assertEquals("john", userDAO.currentUserSet);
    }


    private static class FakeUserDAO implements LoginUserDataAccessInterface {

        boolean exists = false;
        User userToReturn = null;

        String currentUserSet = null;

        @Override
        public boolean existsByName(String username) {
            return exists;
        }

        @Override
        public void save(User user) {
            // Not needed for login tests, but required by interface
            this.userToReturn = user;
        }

        @Override
        public User get(String username) {
            return userToReturn;
        }

        @Override
        public void setCurrentUsername(String name) {
            this.currentUserSet = name;
        }

        @Override
        public String getCurrentUsername() {
            return currentUserSet;
        }
    }

    private static class FakeLoginPresenter implements LoginOutputBoundary {

        String failMessage = null;
        LoginOutputData successData = null;

        @Override
        public void prepareSuccessView(LoginOutputData data) {
            this.successData = data;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failMessage = errorMessage;
        }
    }
}
