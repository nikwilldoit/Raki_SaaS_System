## CONVENTIONS.md file
<p align="justify">
This document describes the established conventions and rules that our team follows when working on the project using GitHub. Its goal is to keep our workflow consistent, organized, and easy for everyone to understand. By following these conventions, we can reduce mistakes, make collaboration smoother, and maintain a clean project structure.
</p>

## The content of the documentation
1. [Short-Lived Branch Names](#short-lived-branch-names)  
2. [Commit Message Naming](#commit-message-naming)  
3. [Issue Conventions](#issue-conventions)  
4. [Coding Conventions](#coding-conventions)  
   - [Classes and Interfaces](#classes-and-interfaces)
   - [Tests](#tests)
   - [Methods](#methods)
   - [Variables](#variables)
   - [Constants](#constants)
   - [Errors](#errors)
   - [Comments](#comments)
   - [Spaces and Braces](#spaces-and-braces)
5. [Repository Structure](#repository-structure)  
6. [General Collaboration Guidelines](#general-collaboration-guidelines)  
7. [Updates to This Document](#updates-to-this-document)

## Short-Lived Branch Names
<p align="justify">
To keep our workflow efficient and organized, all branches should be short-lived, meaning they are created for a specific task and merged as soon as the task is completed. This helps prevent outdated code, reduces merge conflicts, and keeps the repository clean.
</p>

### Naming Convention
Branch names should use **lowercase letters** and **hyphens (`-`)** to separate words. Keep them short, clear, and descriptive, avoiding unnecessary terms. Use the format: **`{issue-type}/{a-few-keywords}`**.

**Possible issue types:** `task`, `fix`, `update`, `feature`.

**Examples:**
```
task/establish-conventions
update/refactoring-readme-file
feature/add-login-page
```
## Commit Message Naming
<p align="justify">
Consistent commit messages make it easier to understand the history of the project and track changes.
Each commit should clearly describe what was done, written in a short and meaningful way.
This helps both current and future team members quickly identify the purpose of each change. Start the description with a lowercase letter and make sure it clearly and specifically explains what was changed.
</p>

Use the following format for commit messages:  
**`{type}: {short-description}{#issue-number}`**

**Common types:**
- `feature` – new feature or functionality  
- `fix` – bug fix  
- `docs` – documentation changes  
- `refactor` – code improvement
- `test` – adding or updating tests
- etc.

## Issue Conventions
<p align="justify">
Issues are used to track tasks, bugs, improvements, or feature requests within the project. 
Writing clear and well-structured issues helps the team understand what needs to be done, discuss ideas effectively, and keep progress visible and easy to follow.
</p>

### Naming Convention
Issue titles should be short and descriptive, so no one else needs to reask for clarification or guess what the issue is about. For example: **Establish coding conventions**.

### Templates
For consistency and clarity, our team uses predefined **issue templates** for different types of issues:
- **Bug Report** – for reporting problems or unexpected behavior.  
- **Feature Request** – for suggesting new features or improvements.  
- **Task** – for general to-do items or non-technical tasks.

Each issue must be created using the appropriate template and filled out with all required information.  
This ensures that every issue includes the necessary context, clear goals, and is easy for others to understand and follow up on.

### Other Requirements
- **Assign a responsible person and labels:** Always select an assignee and apply relevant labels (e.g., `type-bugFix`, `type-update`, `type-task`).  
- **Specify the issue type:** Choose whether it is a *Task*, *Bug*, or *Feature*.  
- **Set priority and timeline:** Define the priority level (*High*, *Medium*, *Low*) and include both *Start* and *End* dates to help track progress.

## Coding Conventions
<p align="justify">
To make our codebase clear, consistent, and easy to maintain, we follow standard naming and commenting conventions.
These rules help every team member quickly understand the purpose of classes, methods, variables, and other code elements.
</p>

### Classes and Interfaces
#### Classes
Use **PascalCase** for class names - each word starts with a capital letter.  
Format: **`{Noun}{OptionalDescriptor}`**.

**Examples:**
```java
public class Order { ... }
public class Sheet { ... }
public class FileNotFoundException extends Exception { ... }
```
#### Interfaces
Use **PascalCase** for interface names as well.
Format: **`{Adjective/Verb}{OptionalNoun}`**.

**Examples:**
```java
public interface DataProvider { ... }
public interface ReadableFile { ... }
```

### Tests
Each test method name should follow this format:  
**`{methodUnderTest}_{descriptionOfScenario}_{descriptionOfExpectation}`**  

**Example:**
```java
@Test
void add_differentLongDelimiters_addition() {
    String input = "//[;;;]\n1;;;2;;;35;;;3";

    int result = StringCalculator.add(input);

    assertEquals(41, result);
}
```
<p align="justify">
Inside each test method body, a single blank line separates setup, execution, and verification blocks.
Each blank line serves a clear purpose — it visually divides the test into logical parts.
Even though each block may contain only one line in simple tests, this structure helps quickly understand the test flow in more complex cases.
</p>

### Methods

Use **camelCase**. A method name should describe what it does, usually starts with a verb.
The suggested format is: **`{verb}{Details}`**.

**Example:**
```
public void loadCustomerData() { ... }
public int computeTotalStickers() { ... }
```

### Variables
<p align="justify">
Variables: use lowerCamelCase (a naming convention in which a name contains multiple words that are joined together as a single word).
The variable name starts with a lowercase letter and the next word begins with uppercase.
Variable names should avoid starting with an underscore (_) or any other sign (even if it's allowed). Using unclear or meaningless names should be avoided.
</p>

**Example:**
```java
int sheetWidth;
String customerName;
```

### Constants

Write constants in **UPPERCASE**, separating words with underscores (_).

Format: **`{MODULE}_{PURPOSE}_{DETAIL}`**

**Example:**

```java
public class User {
    public static final int MAX_LOGIN_ATTEMPTS = 3;
    public static final String DEFAULT_ROLE = "guest";
}
```

### Errors
<p align="justify">
Proper error handling helps identify problems quickly and keeps the application stable. Exceptions and error messages should always be clear, specific, and meaningful.
</p>

- Use **descriptive exception class names** that explain the problem (e.g., `InvalidOrderException`, `FileReadException`).  
- Avoid using generic exceptions such as `Exception` or `Throwable`.  
- When throwing an exception, include a helpful and specific message that describes what went wrong.  

**Example:**
```java
throw new InvalidOrderException("Order ID cannot be null");
throw new FileReadException("Failed to read order file: " + fileName);
```

### Comments
<p align="justify">
The comments should explain why the code is written that way, not how it works, because the code itself should already make that clear. 
Comments don’t need to be long, they should be short, clear, and informative, explaining only what’s necessary for understanding the code, in other words,
they should add value, not repeat what the code already shows. There are 3 types of comments: Javadoc, block, single-line comment. 
</p>

```java
// This is a single line comment. Write it for quick notes

/*
 * This is a regular block comment. Block comments are used to provide descriptions of files, methods, data structures and algorithms.
 */

/**
 * This is a Javadoc. Javadoc comments may be placed above any class, method, or field which we want to document.
 */
```

### Spaces and Braces
<p align="justify">
Using spaces and braces in the same way helps keep the code clean, structured and easy to read. It makes everyone’s code look similar, so the project feels more organized. Good formatting also helps find mistakes faster when checking the code.
</p>

We will use IntelliJ’s automatic settings for braces to keep the code style consistent and avoid mistakes caused by inattention.
```java
public class ShowingExample {
}
```

We will use spaces to make the code easier to read and understand. They help separate elements clearly, so the code looks cleaner and more organized.
```java
if ( ... ) {
...
}
```

## Repository structure
<p align="justify">
A clear and consistent repository structure helps all team members understand where to find specific files, maintain order, and avoid duplication. Below is the general layout of our project repository:
</p>

```
/
/src           -- source code and resources needed to build a working application
/docs          -- permanent documentation (i.e., which would have long-term value)
.gitattributes -- a file that informs git which files are textual, and which are binary
.gitignore     -- a file that informs git which files should not be tracked in version control
README.md      -- an Markdown file that serves as an entry-point/introduction/overview of all things related to your project
```
## General Collaboration Guidelines
<p align="justify">
Good teamwork helps our project run smoothly and stay organized. These rules are here to make sure everyone communicates clearly, works in the same way, and supports each other.
</p>

### Communication
- Be clear and respectful when writing messages or comments.  
- Talk with the team before making big changes to avoid doing the same work twice.  
- Use GitHub issues and pull requests to keep track of progress and decisions.  
- Share your ideas and write feedback — this helps avoid misunderstandings.  
- Ask for help or feedback early instead of waiting until the end.

### Teamwork
- Help teammates when you can, especially with Git or setup problems.  
- Review pull requests and reply to comments on time.  
- Keep your assigned tasks up to date and close issues when they’re finished.  
- Be open to other people’s opinions and suggestions - we all work toward the same goal.

## Updates to This Document
This document may be updated as the team’s workflow evolves. All proposed changes should be discussed and agreed upon before merging into `main`.
