package eu.mostserene.avogador.exerciseservice.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import eu.mostserene.avogador.exerciseservice.abstractexercises.codingexercises.CodingExercise
import eu.mostserene.avogador.exerciseservice.courses.CourseDetailDto
import eu.mostserene.avogador.exerciseservice.courses.CourseDto
import eu.mostserene.avogador.exerciseservice.courses.CourseRole
import eu.mostserene.avogador.exerciseservice.practices.Practice
import eu.mostserene.avogador.exerciseservice.testcases.Testcase
import eu.mostserene.avogador.exerciseservice.testcases.TestcaseDetailDto
import eu.mostserene.avogador.exerciseservice.trials.ProgrammingLanguage
import eu.mostserene.avogador.exerciseservice.trials.Trial
import eu.mostserene.avogador.exerciseservice.users.UserDto
import eu.mostserene.avogador.exerciseservice.usertrials.UserTrial
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Stream

abstract class AbstractControllerTests {

    companion object {
        val mapper: ObjectWriter = ObjectMapper().writer().withDefaultPrettyPrinter()

        val courseId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")
        val archivedCourseId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000002")
        val emptyId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

        val empty = UserDto(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "ryan.howard@avogador.com",
            "Ryan",
            "Howard",
            false,
            false
        )
        val external = UserDto(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "creed.bratton@avogador.com",
            "Creed",
            "Bratton",
            false,
            false
        )
        val student = UserDto(
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            "andy.bernard@avogador.com",
            "Andy",
            "Bernard",
            false,
            false
        )
        val collaborator = UserDto(
            UUID.fromString("00000000-0000-0000-0000-000000000003"),
            "dwigth.schrute@avogador.com",
            "Dwight",
            "Schrute",
            false,
            false
        )
        val professor = UserDto(
            UUID.fromString("00000000-0000-0000-0000-000000000004"),
            "jim.halpert@avogador.com",
            "Jim",
            "Halpert",
            true,
            false
        )
        val superuser = UserDto(
            UUID.fromString("00000000-0000-0000-0000-000000000005"),
            "michael.scott@avogador.com",
            "Michael",
            "Scott",
            false,
            true
        )
        val emptyHeader: String = mapper.writeValueAsString(empty)
        val externalHeader: String = mapper.writeValueAsString(external)
        val studentHeader: String = mapper.writeValueAsString(student)
        val collaboratorHeader: String = mapper.writeValueAsString(collaborator)
        val professorHeader: String = mapper.writeValueAsString(professor)
        val superuserHeader: String = mapper.writeValueAsString(superuser)

        val practice = Practice(
            courseId,
            "Trial",
            true,
            true,
            ProgrammingLanguage.JAVA,
            Date.from(Instant.now().plus(1, ChronoUnit.MINUTES)),
            Date.from(Instant.now().plus(1, ChronoUnit.DAYS))
        )
        val hiddenPractice = Practice(
            courseId,
            "Trial",
            false,
            true,
            ProgrammingLanguage.JAVA,
            Date.from(Instant.now().plus(1, ChronoUnit.MINUTES)),
            Date.from(Instant.now().plus(1, ChronoUnit.DAYS))
        )
        val practiceStartingPast = Practice(
            courseId,
            "Trial",
            true,
            true,
            ProgrammingLanguage.JAVA,
            Date.from(Instant.now().plus(-1, ChronoUnit.MINUTES)),
            Date.from(Instant.now().plus(1, ChronoUnit.DAYS))
        )
        val practiceEndingBeforeStart = Practice(
            courseId,
            "Trial",
            true,
            true,
            ProgrammingLanguage.JAVA,
            Date.from(Instant.now().plus(12, ChronoUnit.DAYS)),
            Date.from(Instant.now().plus(1, ChronoUnit.DAYS))
        )
        val oldPractice = Practice(
            courseId,
            "Trial",
            true,
            true,
            ProgrammingLanguage.JAVA,
            Date.from(Instant.now().plus(-8, ChronoUnit.DAYS)),
            Date.from(Instant.now().plus(6, ChronoUnit.DAYS))
        )
        val practiceInArchivedCourse = Practice(
            archivedCourseId,
            "Trial",
            true,
            true,
            ProgrammingLanguage.JAVA,
            Date.from(Instant.now().plus(1, ChronoUnit.MINUTES)),
            Date.from(Instant.now().plus(1, ChronoUnit.DAYS))
        )

        val visibleExercise = CodingExercise(practice, "Exercise1", "statement", true, 1, ProgrammingLanguage.JAVA)
        val hiddenExercise = CodingExercise(practice, "Exercise2", "statement", false, 1, ProgrammingLanguage.JAVA)

        val visibleTestcase = Testcase(visibleExercise, true, 1, 1.0, "Test")

        val visibleTestcaseDto = TestcaseDetailDto(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            true,
            1,
            "in1",
            "out1"
        )
        val hiddenTestcaseDto = TestcaseDetailDto(
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            false,
            2,
            "in2",
            "out2"
        )
        val simpleVisibleTestcase = Testcase(visibleExercise, true, 1)
        val simpleHiddenTestcase = Testcase(visibleExercise, false, 2)

        val studentPractice = UserTrial(student.id, practice, false)

        val courseDetailDtoExternal = CourseDetailDto(
            courseId,
            "Course Name",
            "2023/2024",
            false,
            CourseRole.EXTERNAL
        )

        val courseDetailDtoStudent = CourseDetailDto(
            courseId,
            "Course Name",
            "2023/2024",
            false,
            CourseRole.STUDENT
        )

        val courseDetailDtoCollaborator = CourseDetailDto(
            courseId,
            "Course Name",
            "2023/2024",
            false,
            CourseRole.COLLABORATOR
        )

        val courseDetailDtoAdmin = CourseDetailDto(
            courseId,
            "Course Name",
            "2023/2024",
            false,
            CourseRole.ADMIN
        )

        val archivedCourseDetailDtoAdmin = CourseDetailDto(
            archivedCourseId,
            "Archived Course Name",
            "2023/2024",
            true,
            CourseRole.ADMIN
        )

        val course = CourseDto()
        val archivedCourse = CourseDto()


        init {
            val exerciseId = CodingExercise::class.java.superclass.getDeclaredField("id")
            exerciseId.isAccessible = true
            exerciseId.set(visibleExercise, UUID.fromString("00000000-0000-0000-0000-000000000001"))
            exerciseId.set(hiddenExercise, UUID.fromString("00000000-0000-0000-0000-000000000002"))

            val practiceId = Trial::class.java.getDeclaredField("id")
            practiceId.isAccessible = true
            practiceId.set(practice, UUID.fromString("00000000-0000-0000-0000-000000000001"))
            practiceId.set(
                practiceStartingPast,
                UUID.fromString("00000000-0000-0000-0000-000000000001")
            ) // same id as practice for update queries
            practiceId.set(practiceEndingBeforeStart, UUID.fromString("00000000-0000-0000-0000-000000000001"))
            practiceId.set(hiddenPractice, UUID.fromString("00000000-0000-0000-0000-000000000002"))
            practiceId.set(oldPractice, UUID.fromString("00000000-0000-0000-0000-000000000003"))
            practiceId.set(practiceInArchivedCourse, UUID.fromString("00000000-0000-0000-0000-000000000004"))

            val testcaseId = Testcase::class.java.getDeclaredField("id")
            testcaseId.isAccessible = true
            testcaseId.set(simpleVisibleTestcase, UUID.fromString("00000000-0000-0000-0000-000000000001"))
            testcaseId.set(simpleHiddenTestcase, UUID.fromString("00000000-0000-0000-0000-000000000002"))

            course.id = courseId
            archivedCourse.id = archivedCourseId
        }

    }

    class CourseMemberHeadersProvider : ArgumentsProvider, AbstractControllerTests() {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            Arguments.of(studentHeader),
            Arguments.of(collaboratorHeader),
            Arguments.of(professorHeader),
            Arguments.of(superuserHeader)
        )
    }

    class CourseExternalHeadersProvider : ArgumentsProvider, AbstractControllerTests() {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            Arguments.of(emptyHeader),
            Arguments.of(externalHeader),
        )
    }

    class PrivilegedUserHeadersProvider : ArgumentsProvider, AbstractControllerTests() {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            Arguments.of(collaboratorHeader),
            Arguments.of(professorHeader),
            Arguments.of(superuserHeader)
        )

    }

    class UnprivilegedUserHeadersProvider : ArgumentsProvider, AbstractControllerTests() {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            Arguments.of(emptyHeader),
            Arguments.of(externalHeader),
            Arguments.of(studentHeader),
        )

    }

}