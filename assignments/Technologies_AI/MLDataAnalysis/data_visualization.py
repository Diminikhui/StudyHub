import matplotlib.pyplot as plt
import numpy as np

x = [1,2,3,4,5,6,7]
y = [3.5, 3.8, 4.2, 4.5, 5, 5.5, 7]
plt.plot(x, y)
plt.title('Линейный график')
plt.xlabel('x')
plt.ylabel('y')
plt.grid(True)
plt.show()

plt.scatter(x, y)
plt.title('Диаграмма рассеяния')
plt.xlabel('x')
plt.ylabel('y')
plt.grid(True)
plt.show()

t = np.linspace(0, 10, 51)
f = np.cos(t)
plt.plot(t, f, color='green')
plt.title('График f(t)')
plt.xlabel('t')
plt.ylabel('f')
plt.grid(True)
plt.show()

x = np.linspace(-3,3,51)
y1 = x**2
y2 = 2*x+0.5
y3 = -3*x-1.5
y4 = np.sin(x)
fig, ax = plt.subplots(2,2,figsize=(8,6))
ax[0,0].plot(x,y1); ax[0,0].set_title('y1'); ax[0,0].set_xlim(-5,5)
ax[0,1].plot(x,y2); ax[0,1].set_title('y2')
ax[1,0].plot(x,y3); ax[1,0].set_title('y3')
ax[1,1].plot(x,y4); ax[1,1].set_title('y4')
plt.subplots_adjust(hspace=0.3, wspace=0.3)
plt.show()
