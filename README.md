# 項目簡介

此專案是使用Android Studio開發的計算機，這個計算機可以進行括號與加減乘除的多項式計算！

## "[activity_main.xml](https://github.com/xuexiahanmei/Mycalculator/blob/master/app/src/main/res/layout/activity_main.xml)"部分展示  
Android Studio開發當中的版面是".XML"的檔案格式，我這裡主要使用linear layout來進行布局，各個區塊大小依權重進行分配。而在分配每個按鈕時會將左右空出10dp的距離，導致那些較大的按鈕至中後在布局上會差個幾dp。未來若優化會考慮grid layout與linear layout交叉使用。

<p align="center">
  <img src="https://github.com/xuexiahanmei/Mycalculator/blob/master/doc/pic/docpic1.png" width="550">
</p>

## "[Main_Activity.java](https://github.com/xuexiahanmei/Mycalculator/blob/master/app/src/main/java/com/example/mycalculator/MainActivity.java)"部分展示

這段程式碼展示了一個簡單的 Android 計算器應用程式的主要功能實作。 在 MainActivity.java 中，主要包含了以下幾個部分：

1.介面初始化：在 [onCreate](https://github.com/xuexiahanmei/Mycalculator/blob/master/README.md#onCreate) 中，設定了佈局檔案並初始化了介面元素，如按鈕和文字視圖。

2.按鈕點擊監聽器類別：[CalculatorClickListener](https://github.com/xuexiahanmei/Mycalculator/blob/master/README.md#CalculatorClickListener) 類別實現了按鈕的點擊監聽器接口，根據點擊的按鈕執行相應的操作。

3.計算結果：[calculateResult](https://github.com/xuexiahanmei/Mycalculator/blob/master/README.md#calculateResult) 用於執行計算器的計算操作，並在發生異常時即時返回錯誤信息。

4.其他輔助方法：[isValid](https://github.com/xuexiahanmei/Mycalculator/blob/master/README.md#isValid) 用於驗證輸入的表達式是否合法，calculate 用於執行實際的計算操作。

5.實際的計算操作: calculate 的計算邏輯是使用堆疊的方式，把乘除減轉化成加然後壓入堆疊，最後在一次加總。當遇到括號則是把左括號壓入堆疊並做標記，等遇到右括號時，再將堆疊一一彈出做加總直到標記位子。

###  "onCreate" 
主要進行的是初始化操作，包括設定使用者介面以及為介面上的按鈕設定點擊事件監聽器。

這段代碼中我犧牲了些許效能與擴展性，以增強代碼的可讀性。

```java
protected void onCreate(Bundle savedInstanceState) {

      ...

        // 設置數字按鈕的點擊監聽器
        for (int i = 0; i <= 9; i++) {
            int buttonId = getResources().getIdentifier("btn_" + i, "id", getPackageName());
            Button numberButton = findViewById(buttonId);
            numberButton.setOnClickListener(new CalculatorClickListener());
        }

        // 設置運算符按鈕的點擊監聽器
        findViewById(R.id.btn_add).setOnClickListener(new CalculatorClickListener());
      ...

}
```

###  "CalculatorClickListener" 
主要是負責處理計算器應用程式中按鈕的點擊事件，並根據點擊的按鈕執行相應的操作。

我在這之中寫了一個判斷使其不會出現"2+-*/2"的狀況。

```java
private class CalculatorClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
            ...

            if (id >= R.id.btn_0 && id <= R.id.btn_9) {
                String number = ((Button) v).getText().toString();
                inputStringBuilder.append(number);
            }{
                ...

            }else {
                if (isOperator(lastChar)){
                    inputStringBuilder.setLength(inputStringBuilder.length() - 1);
                }
                
                ...
                
            }
    }
}
    
private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
}

```

###  "calculateResult" 
用於執行計算器的計算操作。 它呼叫了 isValid 驗證輸入的表達式是否合法，然後呼叫 calculate 執行實際的計算，並在發生異常時即時返回錯誤信息。

```java
private void calculateResult(String s) {
        try {
            isValid(s);
            calculate(s);
        } catch (Exception e) {
            resultTextView.setText("Error: " + e.getMessage());
            System.out.println(e.getMessage()); // 使用 System.out.println 來輸出異常信息
        }
}

```

###  "isValid" 
驗證輸入的表達式是否合法，確保左右括號數一致。

```java
public void isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '(') {
                stack.push(')');
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != c) {
                    throw new IllegalArgumentException("Missing \"(\" symbol");
                }
            }
        }
        if (!stack.isEmpty()) {
            throw new IllegalArgumentException("Missing \")\" symbol");
        }
}
```