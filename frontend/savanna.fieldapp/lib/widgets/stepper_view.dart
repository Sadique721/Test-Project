import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class StepperView extends StatelessWidget {
  final double width;
  final int totalSteps;
  final int curStep;
  final Color stepCompleteColor;
  final Color currentStepColor;
  final Color inactiveColor;
  final double lineWidth;

  StepperView({
    Key? key,
    required this.width,
    required this.curStep,
    required this.stepCompleteColor,
    required this.totalSteps,
    required this.inactiveColor,
    required this.currentStepColor,
    required this.lineWidth,
  })  : assert(curStep > 0 == true && curStep <= totalSteps + 1),
        super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.only(
        top: 20.0,
        left: 24.0,
        right: 24.0,
      ),
      width: width,
      child: Row(
          children: _steps(),
        ),

    );
  }

  getCircleColor(i) {
    Color color;
    if (i + 1 < curStep) {
      color = stepCompleteColor;
    } else if (i + 1 == curStep) {
      color = currentStepColor;
    } else {
      color = Colors.white;
    }
    return color;
  }

  getBorderColor(i) {
    Color color;
    if (i + 1 < curStep) {
      color = stepCompleteColor;
    } else if (i + 1 == curStep) {
      color = currentStepColor;
    } else {
      color = inactiveColor;
    }

    return color;
  }

  getLineColor(i) {
    var color = curStep > i + 1
        ? AppTheme.colorGreen.withOpacity(0.8)
        : AppTheme.colorPrimary;
    return color;
  }

  List<Widget> _steps() {
    var list = <Widget>[];
    for (int i = 0; i < totalSteps; i++) {
      //colors according to state
      var circleColor = getCircleColor(i);
      var borderColor = getBorderColor(i);
      var lineColor = getLineColor(i);
      // step circles
      list.add(
        Container(
          height: 25,
          width: 25,
          alignment: Alignment.center,
          decoration: BoxDecoration(
            color: circleColor,
            borderRadius: const BorderRadius.all(Radius.circular(25.0)),
            border: Border.all(
              color: borderColor,
              width: 1.0,
            ),
          ),
          child: getInnerElementOfStepper(i),
        ),
      );
      //line between step circles
      if (i != totalSteps - 1) {
        list.add(
          Expanded(
            child: Container(
              height: lineWidth,
              color: lineColor,
            ),
          ),
        );
      }
    }
    return list;
  }

  Widget getInnerElementOfStepper(index) {
    if (index + 1 < curStep) {
      return const Icon(
        Icons.check,
        color: Colors.white,
        size: 16.0,
      );
    } else if (index + 1 == curStep) {
      return Center(
        child: CustomText(
          title: '$curStep',
          colors: AppTheme.colorWhite,
          textAlign: TextAlign.center,
          fontSize: AppTheme.small,
          fontWeight: FontWeight.w500,
        ),
      );
    } else {
      int itemIndex = index + 1;
      return Center(
        child: CustomText(
          title: '$itemIndex',
          colors: AppTheme.colorGrey,
          textAlign: TextAlign.center,
          fontSize: AppTheme.small,
          fontWeight: FontWeight.w400,
        ),
      );
    }
  }
}
