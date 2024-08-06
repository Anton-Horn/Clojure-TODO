document.addEventListener("DOMContentLoaded", function () {
    const todos = document.querySelector('.todos');
    const noTodos = document.querySelector('.no-todos');

    function fetchTodos() {
        fetch('/todos')
            .then(response => response.json())
            .then(data => {
                todos.innerHTML = '';
                if (data.length > 0) {
                    noTodos.style.display = 'none';
                    data.forEach(todo => {
                        const todoItem = document.createElement('div');
                        const todoLabel = document.createElement('label');
                        const todoItemCheckbox = document.createElement('input');
                        const todoItemText = document.createElement('span');
                        const editIcon = document.createElement('i');
                        const deleteIcon = document.createElement('i');

                        todoItem.classList.add('todo-item');
                        todoLabel.classList.add('todo-label');
                        todoItemCheckbox.type = 'checkbox';
                        todoItemCheckbox.checked = todo.completed;
                        todoItemText.textContent = todo.title;
                        editIcon.classList.add('fa-solid', 'fa-pen-to-square', 'todo-edit');
                        deleteIcon.classList.add('fa-solid', 'fa-trash', 'todo-delete');

                        todoItemCheckbox.addEventListener('change', function () {
                            const updatedTodo = {
                                title: todo.title, completed: this.checked
                            };

                            fetch(`/todos/${todo.id}`, {
                                method: 'PATCH', headers: {
                                    'Content-Type': 'application/json'
                                }, body: JSON.stringify(updatedTodo)
                            })
                                .then(response => {
                                    if (response.status !== 204) {
                                        return response.json().then(data => {
                                            console.error('Error updating todo:', data);
                                        });
                                    }
                                })
                                .catch(error => {
                                    console.error('Error updating todo:', error);
                                });
                        });

                        editIcon.addEventListener('click', function (event) {
                            event.stopPropagation();

                            if (editIcon.classList.contains('todo-edit')) {
                                editIcon.classList.remove('todo-edit', 'fa-pen-to-square');
                                editIcon.classList.add('todo-edit-cancel', 'fa-circle-xmark');

                                const input = document.createElement('input');
                                input.type = 'text';
                                input.value = todo.title;
                                input.classList.add('todo-edit-input');

                                input.addEventListener('keypress', function (e) {
                                    if (e.key === 'Enter') {
                                        const updatedTodo = {
                                            title: input.value, completed: todo.completed
                                        };

                                        fetch(`/todos/${todo.id}`, {
                                            method: 'PATCH', headers: {
                                                'Content-Type': 'application/json'
                                            }, body: JSON.stringify(updatedTodo)
                                        })
                                            .then(response => {
                                                if (response.status === 204) {
                                                    todo.title = input.value;
                                                    todoItemText.textContent = todo.title;
                                                    todoLabel.removeChild(input);
                                                    editIcon.classList.remove('todo-edit-cancel', 'fa-circle-xmark');
                                                    editIcon.classList.add('todo-edit', 'fa-pen-to-square');
                                                } else {
                                                    return response.json().then(data => {
                                                        console.error('Error updating todo:', data);
                                                    });
                                                }
                                            })
                                            .catch(error => {
                                                console.error('Error updating todo:', error);
                                            });
                                    }
                                });

                                todoLabel.appendChild(input);
                                input.focus();

                            } else if (editIcon.classList.contains('todo-edit-cancel')) {
                                const input = todoLabel.querySelector('input[type="text"]');
                                if (input) {
                                    todoLabel.removeChild(input);
                                }
                                editIcon.classList.remove('todo-edit-cancel', 'fa-circle-xmark');
                                editIcon.classList.add('todo-edit', 'fa-pen-to-square');
                            }
                        });

                        deleteIcon.addEventListener('click', function (event) {
                            event.stopPropagation();

                            fetch(`/todos/${todo.id}`, {
                                method: 'DELETE'
                            })
                                .then(response => {
                                    if (response.status === 204) {
                                        todos.removeChild(todoItem);
                                        if (todos.children.length === 0) {
                                            todosWrapper.style.display = 'none';
                                            noTodos.style.display = 'block';
                                        }
                                    } else {
                                        return response.json().then(data => {
                                            console.error('Error deleting todo:', data);
                                        });
                                    }
                                })
                                .catch(error => {
                                    console.error('Error deleting todo:', error);
                                });
                        });

                        todoLabel.appendChild(todoItemCheckbox);
                        todoLabel.appendChild(todoItemText);
                        todoItem.appendChild(todoLabel);
                        todoItem.appendChild(editIcon);
                        todoItem.appendChild(deleteIcon);
                        todos.appendChild(todoItem);
                    });
                } else {
                    noTodos.style.display = 'block';
                }
            })
            .catch(error => {
                console.error('Error fetching todos:', error);
            });
    }

    document.getElementById('add-todo-form').addEventListener('submit', function (event) {
        event.preventDefault();
        const titleInput = document.getElementById('title');
        const newTodo = {
            title: titleInput.value, completed: false
        };

        fetch('/todos', {
            method: 'POST', headers: {
                'Content-Type': 'application/json'
            }, body: JSON.stringify(newTodo)
        })
            .then(response => {
                if (response.status === 204) {
                    titleInput.value = '';
                    fetchTodos();
                } else {
                    return response.json().then(data => {
                        console.error('Error adding todo:', data);
                    });
                }
            })
            .catch(error => {
                console.error('Error adding todo:', error);
            });
    });

    fetchTodos();
});
