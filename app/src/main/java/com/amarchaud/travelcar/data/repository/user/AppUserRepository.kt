package com.amarchaud.travelcar.data.repository.user

import com.amarchaud.travelcar.data.db.UserDao
import com.amarchaud.travelcar.di.DispatcherModule
import com.amarchaud.travelcar.domain.local.user.AppUser
import com.amarchaud.travelcar.utils.translator.UserTranslator.toAppUser
import com.amarchaud.travelcar.utils.translator.UserTranslator.toEntityUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppUserRepository @Inject constructor(
    private val travelUserDao: UserDao,
    @DispatcherModule.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    override fun getUserFlow() = flow {
        travelUserDao.getUserFlow().collect {
            emit(it?.toAppUser())
        }
    }.flowOn(ioDispatcher)

    override suspend fun getUser(): AppUser? = travelUserDao.getUser()?.toAppUser()

    override suspend fun manageUser(appUser: AppUser): Unit = withContext(ioDispatcher) {
        val exist = travelUserDao.getUser()
        if (exist == null) {
            travelUserDao.insertUser(appUser.toEntityUser())
        } else {
            travelUserDao.updateUser(
                exist.apply {
                    this.photoUri = appUser.photoUri
                    this.firstName = appUser.firstName
                    this.lastName = appUser.lastName
                    this.address = appUser.address
                    this.birthday = appUser.birthday
                }
            )
        }
    }

    override suspend fun deleteUser(): Unit = withContext(ioDispatcher) {
        travelUserDao.getUser()?.let {
            travelUserDao.deleteUser(it)
        }
    }
}