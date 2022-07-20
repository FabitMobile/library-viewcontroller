# ViewController

---

##UDF - Unidirectional Data Flow

ViewController - класс обеспечивающий взаимодействие View и Store,
также хранит ссылку на Store

Для того чтобы выполнить какую нибудь логику обработки пользовательского
интерфейска необходимо сформировать Action и оправить его в Store.
Так как ViewController имеет доступ к Store, то необходимо делать это через него.

Для создания события можно использовать метод для этого существует метод
`dispatchAction(action)`. Так же мы должны иметь возможность обновить UI,
после того как отработает логика. Для этого необходимо подписаться на State
во ViewController, и отправить State во View, а он уже обновит UI в
соответствии с состоянием.

![UDF ViewController](https://github.com/FabitMobile/library-viewcontroller/raw/main/readme/udf_viewcontroller.png)

Рис. 1. Создание события и подписка на store

ViewController реализует логику подписки на Store, и обновление View,
вызывая `renderState(state)`. Для того чтобы можно было использовать данный
механизм, необходимо чтобы наша View реализовала интерфейс StateView:

```kotlin
interface StateView<State> {
    fun renderState(state: State)
}
```

Помимо этого ViewController является LifecycleEventObserver для View, другими
словами он знает про жизненый цикл View. Что нам это дает? Самое главное - это
подписка на Store при создании и возобновлении View (create/resume). Когда
наступает событие `onPause` у View, ViewController отписывается от Store.
Таким образом у нас не произойдет ошибки, если не ожидается, что View должна
обновляться из-за смены состояния. При возобновлении (`onResume`), ViewController
заново подпишется на Store и обновит View в соответствии с последним состоянием,
которое может измениться в то время пока View находится в `onPause`. Если View
разрушится (`onDestroy`), то ViewController отпишется от Store, и пошлет ему
команду закрыть все подписки на Action и их обработку, таким образом Store
больше не будет обрабатывать логику и не сменит состояние.

___

###Пример использования вместе с [Dagger Hilt](https://github.com/google/dagger)

- Создаем Store, State, Action
- Создаем модуль для Store
```kotlin
@Module
@InstallIn(ViewControllerComponent::class)
class ExampleModule {

    @Provides
    @ViewControllerScoped
    internal fun provideExampleStore(
        ...
    ): ExampleStore = ExampleStore(
        ...
    )
}
```
- Создаем ViewController
```kotlin
@HiltViewController
class ExampleViewController @Inject constructor(
    store: ExampleStore
): ViewController<ExampleState, ExampleAction, StateView<ExampleState>>(store) {
    
}
```
- Внутри View (Activity, Fragment, CustomView*) получаем ViewController и
инициализируем
```kotlin
private val viewController: ExampleViewController by viewControllers()

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    registerViewController(viewController)
}
```

(*) Для использования с Custom View необходимо самостоятельно позаботиться о
подписке, используя `LifecycleRegistry` или, указвая родителя как `LifecycleOwner`

