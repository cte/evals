def append(list1, list2):
    return list1 + list2


def concat(lists):
    result = []
    for sublist in lists:
        result += sublist
    return result


def filter(function, list):
    return [item for item in list if function(item)]


def length(list):
    count = 0
    for _ in list:
        count += 1
    return count


def map(function, list):
    return [function(item) for item in list]


def foldl(function, list, initial):
    acc = initial
    for item in list:
        acc = function(acc, item)
    return acc


def foldr(function, list, initial):
    acc = initial
    for item in reversed(list):
        acc = function(acc, item)
    return acc


def reverse(list):
    return list[::-1]
