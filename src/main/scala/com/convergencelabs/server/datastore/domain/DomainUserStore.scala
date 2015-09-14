package com.convergencelabs.server.datastore.domain

trait DomainUserStore {
  def createDomainUser(domainUser: DomainUser): DomainUser

  def deleteDomainUser(uid: String): Unit

  def updateDomainUser(domainUser: DomainUser): Unit

  def getDomainUserByUid(uid: String): DomainUser

  def getDomainUsersByUids(uids: List[String]): List[DomainUser]

  def getDomainUserByUsername(username: String): DomainUser

  def getDomainUsersByUsername(usernames: List[String]): List[DomainUser]

  def getDomainUserByEmail(email: String): DomainUser

  def getDomainUsersByEmail(emails: List[String]): List[DomainUser]

  def domainUserExists(username: String): Boolean

  def getAllDomainUsers(): List[DomainUser]
}

case class DomainUser(uid: String, username: String, firstName: String, lastName: String, emails: List[String])