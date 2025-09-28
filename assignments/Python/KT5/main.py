import tkinter as tk

def load_tasks(filename):
    try:
        with open(filename, 'r', encoding='utf-8') as f:
            for line in f:
                if line.strip():
                    tasks.append(line.strip())
    except FileNotFoundError:
        print("Файл с задачами не найден")

def create_widgets():
    global tasks_frame
    main_frame = tk.Frame(root, bg="black")
    main_frame.pack(fill=tk.BOTH, expand=True, padx=2, pady=2)

    tk.Label(main_frame, bg="black", text="Мои текущие задачи", fg="yellow", font=("Arial", 16, "bold")).pack()

    tasks_frame = tk.Frame(main_frame, bg=main_frame["bg"])
    tasks_frame.pack(fill=tk.BOTH, expand=True)

    tk.Button(main_frame, text="Обновить", command=display_tasks).pack(side=tk.BOTTOM, pady=5)

def display_tasks():
    for widget in tasks_frame.winfo_children():
        widget.destroy()

    for task in tasks:
        if task.startswith("Прошло"):
            color = "red"
        elif task.startswith("Прям"):
            color = "orange"
        elif task.startswith("Остал"):
            color = "white"
        else:
            color = "blue"

        task_frame = tk.Frame(tasks_frame, bg=tasks_frame["bg"])
        task_frame.pack(fill=tk.X, pady=2)

        tk.Label(task_frame, text=task, fg=color, bg=task_frame["bg"], font=("Arial", 11)).pack()

root = tk.Tk()
root.title("Простой менеджер задач")
root.geometry("600x400")

tasks = []
tasks_frame = None

load_tasks("tasks.txt")
create_widgets()
display_tasks()



root.mainloop()