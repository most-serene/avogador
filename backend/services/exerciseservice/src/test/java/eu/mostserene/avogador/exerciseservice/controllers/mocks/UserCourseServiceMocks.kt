package eu.mostserene.avogador.exerciseservice.controllers.mocks

import eu.mostserene.avogador.exerciseservice.controllers.AbstractControllerTests
import eu.mostserene.avogador.exerciseservice.courses.UserCourseService
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import java.util.*

class UserCourseServiceMocks(private val userCourseService: UserCourseService) : AbstractControllerTests() {

    fun setup() {
        setupGetMember()
        setupGetCollaborator()
        setupGetAdmin()
    }

    private fun setupGetMember() {
        `when`(userCourseService.getCourseMember(any(), any()))
            .thenReturn(Optional.empty())
        `when`(userCourseService.getCourseMember(eq(course.id), eq(external)))
            .thenReturn(Optional.empty())
        `when`(userCourseService.getCourseMember(eq(course.id), eq(superuser)))
            .thenReturn(Optional.of(courseDetailDtoExternal))
        `when`(userCourseService.getCourseMember(eq(course.id), eq(student)))
            .thenReturn(Optional.of(courseDetailDtoStudent))
        `when`(userCourseService.getCourseMember(eq(course.id), eq(collaborator)))
            .thenReturn(Optional.of(courseDetailDtoCollaborator))
        `when`(userCourseService.getCourseMember(eq(course.id), eq(professor)))
            .thenReturn(Optional.of(courseDetailDtoAdmin))
        `when`(userCourseService.getCourseMember(eq(archivedCourse.id), eq(professor)))
            .thenReturn(Optional.of(archivedCourseDetailDtoAdmin))
    }

    private fun setupGetCollaborator() {
        `when`(userCourseService.getCourseCollaborator(any(), any()))
            .thenReturn(Optional.empty())
        `when`(userCourseService.getCourseCollaborator(eq(course.id), eq(external)))
            .thenReturn(Optional.empty())
        `when`(userCourseService.getCourseCollaborator(eq(course.id), eq(superuser)))
            .thenReturn(Optional.of(courseDetailDtoExternal))
        `when`(userCourseService.getCourseCollaborator(eq(course.id), eq(student)))
            .thenReturn(Optional.empty())
        `when`(userCourseService.getCourseCollaborator(eq(course.id), eq(collaborator)))
            .thenReturn(Optional.of(courseDetailDtoCollaborator))
        `when`(userCourseService.getCourseCollaborator(eq(course.id), eq(professor)))
            .thenReturn(Optional.of(courseDetailDtoAdmin))
        `when`(userCourseService.getCourseCollaborator(eq(archivedCourse.id), eq(professor)))
            .thenReturn(Optional.of(archivedCourseDetailDtoAdmin))
        // used in UpdatePractice.`(400) different course id`
        `when`(
            userCourseService.getCourseCollaborator(
                eq(UUID.fromString("00000000-0000-0000-0000-000000000000")),
                eq(professor)
            )
        )
            .thenReturn(Optional.of(courseDetailDtoAdmin))
    }

    private fun setupGetAdmin() {
        `when`(userCourseService.getCourseAdmin(any(), any()))
            .thenReturn(Optional.empty())
        `when`(userCourseService.getCourseAdmin(eq(course.id), eq(external)))
            .thenReturn(Optional.empty())
        `when`(userCourseService.getCourseAdmin(eq(course.id), eq(superuser)))
            .thenReturn(Optional.of(courseDetailDtoExternal))
        `when`(userCourseService.getCourseAdmin(eq(course.id), eq(student)))
            .thenReturn(Optional.empty())
        `when`(userCourseService.getCourseAdmin(eq(course.id), eq(collaborator)))
            .thenReturn(Optional.empty())
        `when`(userCourseService.getCourseAdmin(eq(course.id), eq(professor)))
            .thenReturn(Optional.of(courseDetailDtoAdmin))
        `when`(userCourseService.getCourseAdmin(eq(archivedCourse.id), eq(professor)))
            .thenReturn(Optional.of(archivedCourseDetailDtoAdmin))
    }
}