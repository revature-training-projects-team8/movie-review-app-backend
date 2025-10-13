package com.moviereview.service;

public class UserServiceTest {

    @org.junit.jupiter.api.Test
    public void getAllUsers_returnsList() {
        com.moviereview.user.repository.UserRepository userRepository =
            org.mockito.Mockito.mock(com.moviereview.user.repository.UserRepository.class);

        com.moviereview.user.model.User u1 = new com.moviereview.user.model.User();
        u1.setEmail("a@example.com");
        u1.setPassword("pass1");

        com.moviereview.user.model.User u2 = new com.moviereview.user.model.User();
        u2.setEmail("b@example.com");
        u2.setPassword("pass2");

        java.util.List<com.moviereview.user.model.User> mockedList =
            java.util.Arrays.asList(u1, u2);

        org.mockito.Mockito.when(userRepository.findAll()).thenReturn(mockedList);

        com.moviereview.user.service.UserService service =
            new com.moviereview.user.service.UserService(userRepository);

        java.util.List<com.moviereview.user.model.User> result = service.getAllUsers();

        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertEquals(2, result.size());
        org.junit.jupiter.api.Assertions.assertEquals("a@example.com", result.get(0).getEmail());
        org.junit.jupiter.api.Assertions.assertEquals("b@example.com", result.get(1).getEmail());
    }

    @org.junit.jupiter.api.Test
    public void createUser_throwsDuplicateEmail_whenGlobalExceptionNotNull() {
        com.moviereview.user.repository.UserRepository userRepository =
            org.mockito.Mockito.mock(com.moviereview.user.repository.UserRepository.class);
        com.moviereview.exception.GlobalExceptionHandler globalException =
            org.mockito.Mockito.mock(com.moviereview.exception.GlobalExceptionHandler.class);

        com.moviereview.user.model.User user = new com.moviereview.user.model.User();
        user.setEmail("dup@example.com");
        user.setPassword("pwd");

        com.moviereview.user.service.UserService service =
            new com.moviereview.user.service.UserService(userRepository);

        org.junit.jupiter.api.Assertions.assertThrows(
            com.moviereview.exception.DuplicateEmailException.class,
            () -> service.createUser(user)
        );

        org.mockito.Mockito.verify(userRepository, org.mockito.Mockito.never())
            .save(org.mockito.ArgumentMatchers.any(com.moviereview.user.model.User.class));
    }

    @org.junit.jupiter.api.Test
    public void createUser_savesAndReturns_whenGlobalExceptionNull() {
        com.moviereview.user.repository.UserRepository userRepository =
            org.mockito.Mockito.mock(com.moviereview.user.repository.UserRepository.class);
        com.moviereview.exception.GlobalExceptionHandler globalException = null;

        com.moviereview.user.model.User user = new com.moviereview.user.model.User();
        user.setEmail("new@example.com");
        user.setPassword("pwd");

        org.mockito.Mockito.when(userRepository.save(org.mockito.ArgumentMatchers.any(com.moviereview.user.model.User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        com.moviereview.user.service.UserService service =
            new com.moviereview.user.service.UserService(userRepository);

        com.moviereview.user.model.User result = service.createUser(user);

        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertEquals("new@example.com", result.getEmail());
        org.mockito.Mockito.verify(userRepository, org.mockito.Mockito.times(1)).save(user);
    }

    @org.junit.jupiter.api.Test
    public void loginUser_returnsSuccess_onMatchingCredentials() {
        com.moviereview.user.repository.UserRepository userRepository =
            org.mockito.Mockito.mock(com.moviereview.user.repository.UserRepository.class);
        com.moviereview.exception.GlobalExceptionHandler globalException = null;

        com.moviereview.user.model.User user = new com.moviereview.user.model.User();
        user.setEmail("login@example.com");
        user.setPassword("secret");

        org.mockito.Mockito.when(userRepository.findAll())
            .thenReturn(java.util.Collections.singletonList(user));

        com.moviereview.user.service.UserService service =
            new com.moviereview.user.service.UserService(userRepository);

        String result = service.loginUser("login@example.com", "secret");

        org.junit.jupiter.api.Assertions.assertEquals("Login successful", result);
    }

    @org.junit.jupiter.api.Test
    public void loginUser_throwsEmailOrPasswordException_onNoMatch() {
        com.moviereview.user.repository.UserRepository userRepository =
            org.mockito.Mockito.mock(com.moviereview.user.repository.UserRepository.class);
        com.moviereview.exception.GlobalExceptionHandler globalException = null;

        com.moviereview.user.model.User user = new com.moviereview.user.model.User();
        user.setEmail("other@example.com");
        user.setPassword("nop");

        org.mockito.Mockito.when(userRepository.findAll())
            .thenReturn(java.util.Collections.singletonList(user));

        com.moviereview.user.service.UserService service =
            new com.moviereview.user.service.UserService(userRepository);

        org.junit.jupiter.api.Assertions.assertThrows(
            com.moviereview.exception.EmailOrPasswordException.class,
            () -> service.loginUser("doesnot@example.com", "wrong")
        );
    }
}