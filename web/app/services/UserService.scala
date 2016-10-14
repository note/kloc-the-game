package services

import java.util.UUID

import scala.collection.mutable.{Map => MutableMap}
import models.User

import scala.util.Random

trait UserService {
  // TODO: change it to UUID
  type UserId = String

  def registerUser(userName: String): UserId
  def existsUserId(userId: UserId): Boolean
  def getUserById(userId: UserId): User
}

class InMemoryUserService extends UserService {
  private val users = MutableMap[UserId, User]()
  private val random = new Random(System.currentTimeMillis())

  def registerUser(userName: String): UserId = {
    val userId = UUID.randomUUID().toString
    users += (userId -> User(userName, userId))
    userId
  }

  def existsUserId(userId: UserId): Boolean = {
    users.contains(userId)
  }

  def getUserById(userId: UserId): User = {
    users(userId)
  }
}
