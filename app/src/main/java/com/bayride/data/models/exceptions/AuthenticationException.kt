package com.bayride.data.models.exceptions

sealed class AuthenticationException : Exception()

class InvalidFirstNameException : AuthenticationException()

class EmptyFirstNameException : AuthenticationException()

class InvalidLastNameException : AuthenticationException()

class EmptyLastNameException : AuthenticationException()

class InvalidEmailException : AuthenticationException()

class EmptyEmailException : AuthenticationException()

class InvalidCodeException : AuthenticationException()

class InvalidPasswordNotMatchException : AuthenticationException()

class InvalidPasswordException : AuthenticationException()

class EmptyPasswordException : AuthenticationException()

class StrongPasswordException : AuthenticationException()

class ExpiredSessionException : AuthenticationException()

class HaveNotSetUpLocationWarning : AuthenticationException()

class EmptyConfirmPasswordException : AuthenticationException()
