package com.example.studysmart.data.repository

import com.example.studysmart.data.local.TaskDao
import com.example.studysmart.domain.model.Task
import com.example.studysmart.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }

    override fun getUpcomingTasksForSubject(subjectInt: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectInt)
            .map { task -> task.filter { it.isCompleted.not() } }
            .map { task -> sortTasks(task) }
    }

    override fun getCompletedTasksForSubject(subjectInt: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectInt)
            .map { task -> task.filter { it.isCompleted } }
            .map { task -> sortTasks(task) }
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
            .map { tasks -> tasks.filter { it.isCompleted.not() } }
            .map { tasks -> sortTasks(tasks) }
    }

    private fun sortTasks(tasks: List<Task>): List<Task> {
        return tasks.sortedWith(compareBy<Task> { it.dueDate }.thenByDescending { it.priority })
    }
}